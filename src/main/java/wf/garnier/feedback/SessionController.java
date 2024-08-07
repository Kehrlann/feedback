package wf.garnier.feedback;

import java.util.Comparator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	String session(@PathVariable("sessionId") String sessionId, Model model) {
		var session = this.sessionRepository.findSessionBySessionId(sessionId).get();
		model.addAttribute("currentSession", session);
		return "session";
	}

}
