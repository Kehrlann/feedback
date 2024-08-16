package wf.garnier.feedback;

import java.time.Duration;
import java.util.Comparator;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
class SessionController {

	private final SessionRepository sessionRepository;

	SessionController(SessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	@GetMapping("/")
	String index(Model model) {
		var sessions = this.sessionRepository.findSessionByActiveEquals(true)
			.stream()
			.sorted(Comparator.comparing(Session::getName))
			.toList();
		model.addAttribute("sessions", sessions);
		return "index";
	}

	@GetMapping("/session/{sessionId}")
	String session(@PathVariable("sessionId") String sessionId,
			@CookieValue(value = "voter-id", required = false) String userId, Model model,
			HttpServletResponse response) {
		if (userId == null || userId.isBlank()) {
			userId = UUID.randomUUID().toString();
		}
		var cookie = new Cookie("voter-id", userId);
		cookie.setPath("/session");
		cookie.setHttpOnly(true);
		cookie.setMaxAge((int) Duration.ofDays(7).toSeconds());
		response.addCookie(cookie);

		var session = this.sessionRepository.findSessionBySessionId(sessionId).get();
		model.addAttribute("currentSession", session);
		return "session";
	}

}
