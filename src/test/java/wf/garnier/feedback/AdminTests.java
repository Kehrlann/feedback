package wf.garnier.feedback;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

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
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(EmulatorConfiguration.class)
class AdminTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	WebClient webClient;

	@Autowired
	SessionRepository sessionRepository;

	@Autowired
	LocalDatastoreHelper datastoreHelper;

	@BeforeEach
	void setUp() throws IOException {
		datastoreHelper.reset();
		sessionRepository.save(new Session("Test session", true));
		sessionRepository.save(new Session("Other test session", true));
		sessionRepository.save(new Session("Inactive session", false));
	}

	@Test
	void accessAnonymous() throws Exception {
		mvc.perform(get("/admin/").with(anonymous())).andExpect(status().is3xxRedirection());

	}

	@Test
	void accessAllowList() throws Exception {
		mvc.perform(get("/admin/").with(user("alice@example.com"))).andExpect(status().is2xxSuccessful());
		mvc.perform(get("/admin/").with(user("bob@example.com"))).andExpect(status().is2xxSuccessful());
		mvc.perform(get("/admin/").with(user("carol@example.com"))).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser("alice@example.com")
	void listsAllSessions() throws Exception {
		HtmlPage htmlPage = webClient.getPage("/admin/");

		var sessions = getSessionTitles(htmlPage).toList();

		assertThat(sessions).containsExactly("Inactive session", "Other test session", "Test session");
	}

	@Test
	void addSessionIsProtected() throws Exception {
		mvc.perform(post("/admin/session").param("name", "new session")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser("alice@example.com")
	void addSession() throws Exception {
		var sessionTitle = "test-session " + UUID.randomUUID();
		HtmlPage htmlPage = webClient.getPage("/admin/");

		HtmlInput newSessionField = htmlPage.querySelector("#new-session-name");
		HtmlButton addSessionButton = htmlPage.querySelector("#add-session");

		newSessionField.type(sessionTitle);
		htmlPage = addSessionButton.click();

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
	void postSessionBlankTitle() throws Exception {
		mvc.perform(post("/admin/session").with(csrf())).andExpect(status().isBadRequest());
		mvc.perform(post("/admin/session").param("name", "   ").with(csrf())).andExpect(status().isBadRequest());
		mvc.perform(post("/admin/session").param("name", "	").with(csrf())).andExpect(status().isBadRequest());
		mvc.perform(post("/admin/session").param("name", "").with(csrf())).andExpect(status().isBadRequest());
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
			.<HtmlButton>map((list) -> list.querySelector("button"))
			.filter(Objects::nonNull)
			.findFirst();

		assertThat(deleteButton).isPresent();
		htmlPage = deleteButton.get().click();

		assertThat(getSessionTitles(htmlPage)).doesNotContain(sessionTitle);
		assertThat(countSessions(htmlPage)).isEqualTo(initialSessionCount);
	}

	private static int countSessions(HtmlPage htmlPage) {
		return htmlPage.querySelectorAll("li").size();
	}

	private static Stream<String> getSessionTitles(HtmlPage htmlPage) {
		return htmlPage.querySelectorAll("li [data-role=\"description\"]").stream().map(DomNode::getTextContent);
	}

}
