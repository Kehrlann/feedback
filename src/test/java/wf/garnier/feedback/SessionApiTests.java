package wf.garnier.feedback;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionApiTests extends TestBase {

	@Test
	void setsCookie() throws Exception {
		mvc.perform(get("/session/" + savedSession.getSessionId()))
			.andExpect(status().is2xxSuccessful())
			.andExpect(cookie().path("feedback-id", "/session"))
			.andExpect(cookie().maxAge("feedback-id", 60 * 60 * 24));
	}

	@Test
	void resetsCookie() throws Exception {
		Cookie cookie = new Cookie("feedback-id", "test-cookie-id");
		cookie.setPath("/session");
		cookie.setMaxAge(60);
		mvc.perform(get("/session/" + savedSession.getSessionId()).cookie(cookie))
			.andExpect(status().is2xxSuccessful())
			.andExpect(cookie().value("feedback-id", "test-cookie-id"))
			.andExpect(cookie().path("feedback-id", "/session"))
			.andExpect(cookie().maxAge("feedback-id", 60 * 60 * 24));
	}

}
