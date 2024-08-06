package wf.garnier.feedback;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.StreamSupport;

import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(EmulatorConfiguration.class)
class AdminApiTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	SessionRepository sessionRepository;

	@Autowired
	LocalDatastoreHelper datastoreHelper;

	private String LOGIN_URL = "http://localhost/oauth2/authorization/github";

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
	void addSession() throws Exception {
		var sessionName = "session-add " + UUID.randomUUID();
		mvc.perform(post("/admin/session").param("name", sessionName).with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin"));
		assertThat(StreamSupport.stream(sessionRepository.findAll().spliterator(), false)).map(Session::getName)
			.anyMatch(sessionName::equals);
	}

	@Test
	void addSessionIsProtected() throws Exception {
		mvc.perform(post("/admin/session").param("name", "new session").with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(LOGIN_URL));
	}

	@Test
	@WithMockUser("alice@example.com")
	void addSessionBlankTitle() throws Exception {
		mvc.perform(post("/admin/session").with(csrf())).andExpect(status().isBadRequest());
		mvc.perform(post("/admin/session").param("name", "   ").with(csrf())).andExpect(status().isBadRequest());
		mvc.perform(post("/admin/session").param("name", "	").with(csrf())).andExpect(status().isBadRequest());
		mvc.perform(post("/admin/session").param("name", "").with(csrf())).andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser("alice@example.com")
	void deleteSession() throws Exception {
		var savedSession = createSession();

		mvc.perform(post("/admin/session/delete").param("session-id", savedSession.getSessionId()).with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin"));
		assertThat(this.sessionRepository.findSessionBySessionId(savedSession.getSessionId())).isEmpty();
	}

	@Test
	void deleteSessionIsProtected() throws Exception {
		var savedSession = createSession();

		mvc.perform(post("/admin/session/delete").param("session-id", savedSession.getSessionId()).with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(LOGIN_URL));
		assertThat(this.sessionRepository.findSessionBySessionId(savedSession.getSessionId())).isPresent();
	}

	private Session createSession() {
		return createSession(true);
	}

	private Session createSession(boolean active) {
		var activeSession = new Session("test-session-" + UUID.randomUUID(), active);
		return this.sessionRepository.save(activeSession);
	}

}
