package wf.garnier.feedback;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
class SessionController {

	private final SessionRepository sessionRepository;

	private final SessionVoteRepository sessionVoteRepository;

	SessionController(SessionRepository sessionRepository, SessionVoteRepository sessionVoteRepository) {
		this.sessionRepository = sessionRepository;
		this.sessionVoteRepository = sessionVoteRepository;
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

	@GetMapping("/session/{sessionId}/vote")
	@ResponseBody
	List<VoteResponse> vote(@PathVariable("sessionId") String sessionId) {
		return this.sessionVoteRepository.findAllBySessionId(sessionId)
			.stream()
			.map((vote) -> new VoteResponse(vote.getVoterId(), vote.getFeedback()))
			.toList();
	}

	@PostMapping("/session/{sessionId}/vote")
	@ResponseStatus(code = HttpStatus.CREATED)
	void vote(@PathVariable("sessionId") String sessionId, @RequestParam("feedback") String feedback,
			@CookieValue(value = "voter-id", required = true) String voterId) {
		this.sessionVoteRepository.save(new SessionVote(voterId, feedback, sessionId));
	}

	record VoteResponse(String voterId, String feedback) {
	}

}
