package wf.garnier.feedback;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionApiTests extends TestBase {

	Cookie cookie = new Cookie("voter-id", "test-voter");

	@BeforeEach
	@Override
	void setUp() throws java.io.IOException {
		super.setUp();
		cookie.setPath("/session");
	}

	@Test
	void setsCookie() throws Exception {
		mvc.perform(get("/session/" + savedSession.getSessionId()))
			.andExpect(status().is2xxSuccessful())
			.andExpect(cookie().path("voter-id", "/session"))
			.andExpect(cookie().maxAge("voter-id", 60 * 60 * 24 * 7));
	}

	@Test
	void resetsCookie() throws Exception {
		mvc.perform(get("/session/" + savedSession.getSessionId()).cookie(cookie))
			.andExpect(status().is2xxSuccessful())
			.andExpect(cookie().value("voter-id", "test-voter"))
			.andExpect(cookie().path("voter-id", "/session"))
			.andExpect(cookie().maxAge("voter-id", 60 * 60 * 24 * 7));
	}

	@Nested
	class AddVote {

		@Test
		void success() throws Exception {
			mvc.perform(post("/session/" + savedSession.getSessionId() + "/vote").with(csrf())
				.cookie(cookie)
				.param("feedback", "Fun")).andExpect(status().isCreated());

			mvc.perform(post("/session/" + savedSession.getSessionId() + "/vote").with(csrf())
				.cookie(cookie)
				.param("feedback", "Interesting")).andExpect(status().isCreated());

			mvc.perform(get("/session/" + savedSession.getSessionId() + "/vote"))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.length()").value(2));
		}

		@Test
		void sessionDoesNotExist() throws Exception {
			mvc.perform(post("/session/does-not-exist/vote").with(csrf()).cookie(cookie).param("feedback", "Fun"))
				.andExpect(status().isNotFound());
		}

		@Test
		void noCookie() throws Exception {
			mvc.perform(post("/session/" + savedSession.getSessionId() + "/vote").with(csrf()).param("feedback", "Fun"))
				.andExpect(status().isBadRequest());
		}

		@Test
		void invalidFeedbackChoice() throws Exception {
			mvc.perform(post("/session/" + savedSession.getSessionId() + "/vote").with(csrf())
				.cookie(cookie)
				.param("feedback", "This is not allowed")).andExpect(status().isBadRequest());
		}

	}

	@Nested
	class DeleteVote {

		@Test
		void success() throws Exception {
			mvc.perform(post("/session/" + savedSession.getSessionId() + "/vote").with(csrf())
				.cookie(cookie)
				.param("feedback", "Fun")).andExpect(status().isCreated());

			mvc.perform(delete("/session/" + savedSession.getSessionId() + "/vote").with(csrf())
				.cookie(cookie)
				.param("feedback", "Fun")).andExpect(status().isNoContent());

			mvc.perform(get("/session/" + savedSession.getSessionId() + "/vote"))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.length()").value(0));
		}

		@Test
		void sessionDoesNotExist() throws Exception {
			mvc.perform(delete("/session/does-not-exist/vote").with(csrf()).cookie(cookie).param("feedback", "Fun"))
				.andExpect(status().isNotFound());
		}

		@Test
		void noCookie() throws Exception {
			mvc.perform(post("/session/" + savedSession.getSessionId() + "/vote").with(csrf()).param("feedback", "Fun"))
				.andExpect(status().isBadRequest());
		}

		@Test
		void voteDoesNotExist() throws Exception {
			mvc.perform(delete("/session/" + savedSession.getSessionId() + "/vote").with(csrf())
				.cookie(cookie)
				.param("feedback", "This does not exist")).andExpect(status().isNoContent());

		}

	}

}
