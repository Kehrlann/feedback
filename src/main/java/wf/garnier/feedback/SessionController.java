package wf.garnier.feedback;

import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

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
		// Get data
		var loadedSession = this.sessionRepository.findSessionBySessionId(sessionId);
		if (loadedSession.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session does not exist");
		}
		var session = loadedSession.get();
		model.addAttribute("currentSession", session);

		response.addCookie(refreshCookie(userId));

		return "session";
	}

	private static Cookie refreshCookie(String userId) {
		if (userId == null || userId.isBlank()) {
			userId = UUID.randomUUID().toString();
		}
		var cookie = new Cookie("voter-id", userId);
		cookie.setPath("/session");
		cookie.setHttpOnly(true);
		cookie.setMaxAge((int) Duration.ofDays(7).toSeconds());
		return cookie;
	}

	@GetMapping("/session/{sessionId}/vote")
	@ResponseBody
	List<VoteResponse> vote(@PathVariable("sessionId") String sessionId) {
		return this.sessionRepository.findSessionBySessionId(sessionId)
			.stream()
			.map(Session::getVotes)
			.flatMap(Collection::stream)
			.map((vote) -> new VoteResponse(vote.getVoterId(), vote.getFeedback()))
			.toList();
	}

	@PostMapping("/session/{sessionId}/vote")
	@ResponseStatus(code = HttpStatus.CREATED)
	void vote(@PathVariable("sessionId") String sessionId, @RequestParam("feedback") String feedback,
			@CookieValue(value = "voter-id", required = true) String voterId) {
		var loadedSession = this.sessionRepository.findSessionBySessionId(sessionId);
		if (loadedSession.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session does not exist");
		}
		var session = loadedSession.get();
		if (!session.getFeedbackChoices().contains(feedback)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Unknown feedback choice. Please use one of: %s".formatted(session.getFeedbackChoices()));
		}
		session.addVote(voterId, feedback);
		this.sessionRepository.save(session);
	}

	@DeleteMapping("/session/{sessionId}/vote")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void deleteVote(@PathVariable("sessionId") String sessionId, @RequestParam("feedback") String feedback,
			@CookieValue(value = "voter-id", required = true) String voterId) {
		// TODO: use composite-key instead
		var loadedSession = this.sessionRepository.findSessionBySessionId(sessionId);
		if (loadedSession.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session does not exist");
		}
		var session = loadedSession.get();
		session.getVote(voterId, feedback).ifPresent(this.sessionVoteRepository::delete);
	}

	record VoteResponse(String voterId, String feedback) {
	}

}
