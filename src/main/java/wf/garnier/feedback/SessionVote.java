package wf.garnier.feedback;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.google.cloud.datastore.Key;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;
import com.google.cloud.spring.data.datastore.core.mapping.Unindexed;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

@Entity(name = "session_vote")
public class SessionVote {

	@Id
	private Key key;

	@Unindexed
	@Field(name = "voter_id")
	private String voterId;

	@Unindexed
	@NotBlank
	private String feedback;

	@CreatedDate
	@Field(name = "creation_time")
	@Unindexed
	private LocalDateTime creationTime;

	public SessionVote() {
		// keep for spring-data
	}

	SessionVote(String voterId, String feedback) {
		this.voterId = voterId;
		this.feedback = feedback;
		this.creationTime = LocalDateTime.now(ZoneId.of("UTC"));
	}

	public String getVoterId() {
		return this.voterId;
	}

	public void setVoterId(String voterId) {
		this.voterId = voterId;
	}

	public @NotBlank String getFeedback() {
		return this.feedback;
	}

	public void setFeedback(@NotBlank String feedback) {
		this.feedback = feedback;
	}

	public LocalDateTime getCreationTime() {
		return this.creationTime;
	}

	public void setCreationTime(LocalDateTime creationTime) {
		this.creationTime = creationTime;
	}

	public Key getKey() {
		return this.key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

}
