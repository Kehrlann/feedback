package wf.garnier.feedback;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class SessionController {

	private final SessionRepository sessionRepository;

	SessionController(SessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	@GetMapping("/")
	String index(Model model) {
		var sessions = this.sessionRepository.findSessionByActiveEqualsOrderByNameAsc(true);
		model.addAttribute("sessions", sessions);
		return "index";
	}

}
