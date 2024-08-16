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

}
