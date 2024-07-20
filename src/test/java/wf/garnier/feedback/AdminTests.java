package wf.garnier.feedback;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
	void index() throws Exception {
		mvc.perform(get("/admin/").with(user("alice@example.com"))).andExpect(status().is2xxSuccessful());
		mvc.perform(get("/admin/").with(user("bob@example.com"))).andExpect(status().is2xxSuccessful());
		mvc.perform(get("/admin/").with(user("carol@example.com"))).andExpect(status().isForbidden());
	}

}
