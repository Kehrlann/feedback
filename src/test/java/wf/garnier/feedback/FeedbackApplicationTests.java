package wf.garnier.feedback;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(DatastoreConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@ImportTestcontainers(DatastoreConfiguration.class)
class FeedbackApplicationTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	WebClient webClient;

	@Autowired
	SessionRepository sessionRepository;

	@BeforeEach
	void setUp() {
		DatastoreConfiguration.reset();
		sessionRepository.save(new Session("Test session", true));
		sessionRepository.save(new Session("Other test session", true));
	}

	@Test
	void index() throws Exception {
		mvc.perform(get("/")).andExpect(status().is2xxSuccessful());
	}

	@Test
	void listsSessions() throws Exception {
		HtmlPage htmlPage = webClient.getPage("/");

		var sesh = htmlPage.querySelectorAll("li");
		var sessions = sesh.stream().map(DomNode::getTextContent).toList();

		assertThat(sessions).containsExactly("Other test session", "Test session");
	}

	@TestConfiguration
	static class EmulatorConfiguration {

		// By default, autoconfiguration will initialize application default credentials.
		// For testing purposes, don't use any credentials. Bootstrap w/
		// NoCredentialsProvider.
		@Bean
		CredentialsProvider googleCredentials() {
			return NoCredentialsProvider.create();
		}

	}

}
