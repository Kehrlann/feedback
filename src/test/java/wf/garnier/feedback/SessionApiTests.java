package wf.garnier.feedback;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionApiTests extends TestBase {

	@Test
	void setsCookie() throws Exception {
		mvc.perform(get("/session/" + savedSession.getSessionId()))
			.andExpect(status().is2xxSuccessful())
			.andExpect(cookie().path("voter-id", "/session"))
			.andExpect(cookie().maxAge("voter-id", 60 * 60 * 24 * 7));
	}

	@Test
	void resetsCookie() throws Exception {
		Cookie cookie = new Cookie("voter-id", "test-cookie-id");
		cookie.setPath("/session");
		cookie.setMaxAge(60);
		mvc.perform(get("/session/" + savedSession.getSessionId()).cookie(cookie))
			.andExpect(status().is2xxSuccessful())
			.andExpect(cookie().value("voter-id", "test-cookie-id"))
			.andExpect(cookie().path("voter-id", "/session"))
			.andExpect(cookie().maxAge("voter-id", 60 * 60 * 24 * 7));
	}

	@Test
	void addVote() throws Exception {
		Cookie cookie = new Cookie("voter-id", "test-voter");
		cookie.setPath("/session");
		cookie.setMaxAge(60);

		mvc.perform(post("/session/" + savedSession.getSessionId() + "/vote").with(csrf())
			.cookie(cookie)
			.param("feedback", "Fun")).andExpect(status().is2xxSuccessful());

		mvc.perform(get("/session/" + savedSession.getSessionId() + "/vote"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	@Disabled
	void addVoteSessionDoesNotExist() throws Exception {

	}

	@Test
	@Disabled
	void addVoteRequiresCookie() throws Exception {

	}

	@Test
	@Disabled
	void deleteVote() throws Exception {

	}

}
