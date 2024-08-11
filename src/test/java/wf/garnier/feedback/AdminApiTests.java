package wf.garnier.feedback;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminApiTests extends TestBase {

	private static final String LOGIN_URL = "http://localhost/oauth2/authorization/github";

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

	@Test
	@WithMockUser("alice@example.com")
	void toggleActiveSession() throws Exception {
		var savedActiveSession = createSession(true);
		var savedInactiveSession = createSession(false);

		mvc.perform(post("/admin/session/toggle-active").param("session-id", savedActiveSession.getSessionId())
			.param("active", "false")
			.with(csrf())).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin"));

		mvc.perform(post("/admin/session/toggle-active").param("session-id", savedInactiveSession.getSessionId())
			.param("active", "true")
			.with(csrf())).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin"));

		var updatedActiveSession = this.sessionRepository.findSessionBySessionId(savedActiveSession.getSessionId());
		var updatedInactiveSession = this.sessionRepository.findSessionBySessionId(savedInactiveSession.getSessionId());
		assertThat(updatedActiveSession).isPresent().get().extracting(Session::getActive).isEqualTo(false);
		assertThat(updatedInactiveSession).isPresent().get().extracting(Session::getActive).isEqualTo(true);
	}

	@Test
	void toggleActiveSessionIsProtected() throws Exception {
		var savedActiveSession = createSession(true);

		mvc.perform(post("/admin/session/toggle-active").param("session-id", savedActiveSession.getSessionId())
			.param("active", "false")
			.with(csrf())).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(LOGIN_URL));

		var updatedActiveSession = this.sessionRepository.findSessionBySessionId(savedActiveSession.getSessionId());
		assertThat(updatedActiveSession).isPresent().get().extracting(Session::getActive).isEqualTo(true);
	}

	private Session createSession() {
		return createSession(true);
	}

	private Session createSession(boolean active) {
		var activeSession = new Session("test-session-" + UUID.randomUUID(), "", active, Collections.emptyList());
		return this.sessionRepository.save(activeSession);
	}

}
