package wf.garnier.feedback;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(EmulatorConfiguration.class)
class SessionControllerTests {

	@Autowired
	WebClient webClient;

	@Autowired
	SessionRepository sessionRepository;

	@Autowired
	LocalDatastoreHelper datastoreHelper;

	Session session;

	@BeforeEach
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
