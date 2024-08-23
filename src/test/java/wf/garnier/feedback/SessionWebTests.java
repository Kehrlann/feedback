package wf.garnier.feedback;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class SessionWebTests extends TestBase {

	Session session;

	@BeforeEach
	@Override
	void setUp() throws IOException {
		datastoreHelper.reset();
		session = sessionRepository
			.save(new Session("Test session", "Some Conference (2024-04-22)", true, List.of("Good", "Bad")));
	}

	@Test
	void viewSession() throws Exception {
		HtmlPage htmlPage = webClient.getPage("/session/" + session.getSessionId());

		var choices = htmlPage.querySelectorAll("[data-role=\"feedback-choice\"]")
			.stream()
			.map(DomNode::getTextContent);
		assertThat(htmlPage.querySelector("h1").getTextContent()).isEqualTo("Test session");
		assertThat(htmlPage.querySelector("h2").getTextContent()).isEqualTo("Some Conference (2024-04-22)");
		assertThat(choices).containsExactly("Good", "Bad");
	}

	@Test
	void sessionDoesNotExist() throws Exception {
		assertThatExceptionOfType(FailingHttpStatusCodeException.class)
			.isThrownBy(() -> webClient.getPage("/session/does-not-exist"))
			.extracting(FailingHttpStatusCodeException::getStatusCode)
			.isEqualTo(404);
	}

	@Test
	void navigateHome() throws Exception {
		HtmlPage htmlPage = webClient.getPage("/session/" + session.getSessionId());
		htmlPage = htmlPage.getElementById("home").click();
		assertThat(htmlPage.getUrl().getPath()).isEqualTo("/");
	}

}
