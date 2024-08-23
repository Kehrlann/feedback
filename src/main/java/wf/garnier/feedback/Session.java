package wf.garnier.feedback;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.cloud.datastore.Key;
import com.google.cloud.spring.data.datastore.core.mapping.Descendants;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;
import com.google.cloud.spring.data.datastore.core.mapping.Unindexed;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

@Entity(name = "session")
public class Session {

	@Id
	private Key key;

	@Field(name = "session_id")
	private String sessionId;

	@Unindexed
	private String name;

	@Unindexed
	private String description;

	private Boolean active;

	@Descendants
	private List<SessionVote> votes;

	@CreatedDate
	@Field(name = "creation_time")
	@Unindexed
	private LocalDateTime creationTime;

	@Unindexed
	@Field(name = "feedback_choices")
	private List<String> feedbackChoices;

	//@formatter:off
	private static final List<String> DEFAULT_FEEDBACK_CHOICES = List.of(
			"Fun",
			"Interesting",
			"I've learned something new",
			"Good speaker",
			"Not clear",
			"Too complicated",
			"Too fast",
			"Too slow"
	);
	//@formatter:on

	public Session() {
	}

	public Session(String name, String description) {
		this(name, description, true, DEFAULT_FEEDBACK_CHOICES);
	}

	public Session(String name, String description, Boolean active, List<String> feedbackChoices) {
		this.sessionId = UUID.randomUUID().toString();
		this.name = name;
		this.description = description;
		this.active = active;
		this.creationTime = LocalDateTime.now(ZoneId.of("UTC"));
		this.feedbackChoices = feedbackChoices;
	}

	public Key getKey() {
		return this.key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public LocalDateTime getCreationTime() {
		return this.creationTime;
	}

	public void setCreationTime(LocalDateTime creationTime) {
		this.creationTime = creationTime;
	}

	public List<String> getFeedbackChoices() {
		return this.feedbackChoices;
	}

	public void setFeedbackChoices(List<String> feedbackChoices) {
		this.feedbackChoices = feedbackChoices;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SessionVote> getVotes() {
		return this.votes;
	}

	public void setVotes(List<SessionVote> votes) {
		this.votes = votes;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "Session{" +
				"key=" + this.key +
				", sessionId='" + this.sessionId + "'" +
				", name='" + this.name + "'" +
				", description='" + this.description + "'" +
				", active=" + this.active +
				", creationTime=" + this.creationTime +
				", feedbackChoices=" + this.feedbackChoices +
				'}';
		//@formatter:on
	}

	public void addVote(String voterId, String feedback) {
		// TODO: check vote
		this.votes = List.of(new SessionVote(voterId, feedback));
	}

	public Optional<SessionVote> getVote(String voterId, String feedback) {
		return this.votes.stream()
			.filter((vote) -> vote.getVoterId().equals(voterId) && vote.getFeedback().equals(feedback))
			.findFirst();
	}

}
