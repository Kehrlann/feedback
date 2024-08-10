package wf.garnier.feedback;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(EmulatorConfiguration.class)
class AdminWebTests {

	@Autowired
	WebClient webClient;

	@Autowired
	SessionRepository sessionRepository;

	@Autowired
	LocalDatastoreHelper datastoreHelper;

	@BeforeEach
	void setUp() throws IOException {
		datastoreHelper.reset();
		sessionRepository.save(new Session("Test session"));
		sessionRepository.save(new Session("Other test session"));
		sessionRepository.save(new Session("Inactive session", false, Collections.emptyList()));
	}

	@Test
	@WithMockUser("alice@example.com")
	void listsAllSessions() throws Exception {
		HtmlPage htmlPage = webClient.getPage("/admin/");

		var sessions = getSessionTitles(htmlPage).toList();

		assertThat(sessions).containsExactly("Inactive session", "Other test session", "Test session");
	}

	@Test
	@WithMockUser("alice@example.com")
	void addSession() throws Exception {
		var sessionTitle = "test-session " + UUID.randomUUID();
		var htmlPage = addSession(sessionTitle);

		var sessions = getSessionTitles(htmlPage);
		assertThat(sessions).contains(sessionTitle);
	}

	@ParameterizedTest
	@WithMockUser("alice@example.com")
	@ValueSource(strings = { "", "    ", "	" })
	void addSessionBlankTitle(String title) throws Exception {
		HtmlPage htmlPage = webClient.getPage("/admin/");
		var initialSessionCount = countSessions(htmlPage);

		HtmlButton addSessionButton = htmlPage.querySelector("#add-session");
		HtmlInput newSessionField = htmlPage.querySelector("#new-session-name");

		newSessionField.type(title);
		htmlPage = addSessionButton.click();

		var sessions = getSessionTitles(htmlPage);
		var sessionCount = countSessions(htmlPage);
		assertThat(initialSessionCount).isEqualTo(sessionCount);
		assertThat(sessions).allMatch((name) -> !name.isBlank());
	}

	@Test
	@WithMockUser("alice@example.com")
	void deleteSession() throws Exception {
		var sessionTitle = "test-session-delete " + UUID.randomUUID();
		HtmlPage htmlPage = webClient.getPage("/admin/");
		var initialSessionCount = countSessions(htmlPage);

		HtmlInput newSessionField = htmlPage.querySelector("#new-session-name");
		HtmlButton addSessionButton = htmlPage.querySelector("#add-session");

		newSessionField.type(sessionTitle);
		htmlPage = addSessionButton.click();
		assertThat(countSessions(htmlPage)).isEqualTo(initialSessionCount + 1);

		var deleteButton = htmlPage.querySelectorAll("li")
			.stream()
			.filter((list) -> list.getTextContent().contains(sessionTitle))
			.<HtmlButton>map((list) -> list.querySelector("button[data-role=\"delete\"]"))
			.filter(Objects::nonNull)
			.findFirst();

		assertThat(deleteButton).isPresent();
		htmlPage = deleteButton.get().click();

		assertThat(getSessionTitles(htmlPage)).doesNotContain(sessionTitle);
		assertThat(countSessions(htmlPage)).isEqualTo(initialSessionCount);
	}

	@Test
	@WithMockUser("alice@example.com")
	void toggleActiveInactive() throws Exception {
		var sessionTitle = "test-session-active " + UUID.randomUUID();
		var htmlPage = addSession(sessionTitle);

		var newSession = getSessionByTitle(htmlPage, sessionTitle);
		assertThat(newSession).isPresent();
		HtmlButton toggleActiveButton = newSession.get().querySelector("button[data-role=\"toggle-active\"]");
		assertThat(toggleActiveButton.getTextContent()).isEqualTo("Set to inactive");
		assertThat(loadSessionByName(sessionTitle)).extracting(Session::getActive).isEqualTo(true);

		htmlPage = toggleActiveButton.click();

		newSession = getSessionByTitle(htmlPage, sessionTitle);
		assertThat(newSession).isPresent();
		toggleActiveButton = newSession.get().querySelector("button[data-role=\"toggle-active\"]");
		assertThat(toggleActiveButton.getTextContent()).isEqualTo("Set to active");
		assertThat(loadSessionByName(sessionTitle)).extracting(Session::getActive).isEqualTo(false);

		htmlPage = toggleActiveButton.click();

		newSession = getSessionByTitle(htmlPage, sessionTitle);
		assertThat(newSession).isPresent();
		toggleActiveButton = newSession.get().querySelector("button[data-role=\"toggle-active\"]");
		assertThat(toggleActiveButton.getTextContent()).isEqualTo("Set to inactive");
		assertThat(loadSessionByName(sessionTitle)).extracting(Session::getActive).isEqualTo(true);
	}

	private static Optional<DomNode> getSessionByTitle(HtmlPage htmlPage, String sessionTitle) {
		return htmlPage.querySelectorAll("li")
			.stream()
			.filter((node) -> node.getTextContent().contains(sessionTitle))
			.findFirst();
	}

	private Session loadSessionByName(String sessionTitle) {
		var sessionByName = StreamSupport.stream(this.sessionRepository.findAll().spliterator(), false)
			.filter((session) -> session.getName().equals(sessionTitle))
			.findFirst();
		assertThat(sessionByName).withFailMessage("could not find session with title: " + sessionTitle).isPresent();
		return sessionByName.get();
	}

	private static int countSessions(HtmlPage htmlPage) {
		return htmlPage.querySelectorAll("li").size();
	}

	private static Stream<String> getSessionTitles(HtmlPage htmlPage) {
		return htmlPage.querySelectorAll("li [data-role=\"description\"]").stream().map(DomNode::getTextContent);
	}

	private HtmlPage addSession(String sessionTitle) throws IOException {
		HtmlPage htmlPage = webClient.getPage("/admin/");

		HtmlInput newSessionField = htmlPage.querySelector("#new-session-name");
		HtmlButton addSessionButton = htmlPage.querySelector("#add-session");

		newSessionField.type(sessionTitle);
		htmlPage = addSessionButton.click();
		return htmlPage;
	}

}
