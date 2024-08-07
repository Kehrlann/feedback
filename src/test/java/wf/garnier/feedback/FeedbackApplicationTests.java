package wf.garnier.feedback;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
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
class FeedbackApplicationTests {

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
	void listsSessions() throws Exception {
		HtmlPage htmlPage = webClient.getPage("/");

		var sessions = htmlPage.querySelectorAll("li").stream().map(DomNode::getTextContent).toList();

		assertThat(sessions).containsExactly("Other test session", "Test session");
	}

}
