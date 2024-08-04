package wf.garnier.feedback;

import java.util.Comparator;
import java.util.stream.StreamSupport;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

	@PostMapping("/session")
	String newSession(@RequestParam("name") @NotBlank String name) {
		var session = new Session(name, true);
		this.sessionRepository.save(session);
		return "redirect:/admin";
	}

}
