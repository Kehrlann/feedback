package wf.garnier.feedback;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionControllerTests extends TestBase {

	Session session;

	@BeforeEach
	@Override
	void setUp() throws IOException {
		datastoreHelper.reset();
		session = sessionRepository.save(new Session("Test session"));
	}

	@Test
	void viewSession() throws Exception {
		HtmlPage htmlPage = webClient.getPage("/session/" + session.getSessionId());

		assertThat(htmlPage.querySelector("h1").getTextContent()).isEqualTo(session.getName());
	}

	@Test
	void navigateHome() throws Exception {
		HtmlPage htmlPage = webClient.getPage("/session/" + session.getSessionId());
		htmlPage = htmlPage.getElementById("home").click();
		assertThat(htmlPage.getUrl().getPath()).isEqualTo("/");
	}

}
