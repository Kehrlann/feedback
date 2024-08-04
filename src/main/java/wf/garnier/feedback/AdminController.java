package wf.garnier.feedback;

import java.util.Comparator;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
class AdminController {

	private final SessionRepository sessionRepository;

	AdminController(SessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	@GetMapping({ "", "/" })
	String index(Model model) {
		var sessions = StreamSupport.stream(this.sessionRepository.findAll().spliterator(), false)
			.sorted(Comparator.comparing(Session::getName))
			.toList();
		model.addAttribute("sessions", sessions);
		return "admin";
	}

}
