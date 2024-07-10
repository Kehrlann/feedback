package wf.garnier.feedback;

import java.time.LocalDateTime;
import java.util.UUID;

import com.google.cloud.datastore.Key;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

@Entity(name = "session")
public class Session {

	@Id
	private Key key;

	@Field(name = "session_id")
	private String sessionId;

	private String name;

	private Boolean active;

	@CreatedDate
	@Field(name = "creation_time")
	private LocalDateTime creationTime;

	public Session() {
	}

	public Session(String name, Boolean active) {
		this.sessionId = UUID.randomUUID().toString();
		this.name = name;
		this.active = active;
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

	@Override
	public String toString() {
		return "Session{" + "key=" + this.key + ", sessionId='" + this.sessionId + '\'' + ", name='" + this.name + '\''
				+ ", active=" + this.active + ", creationTime=" + this.creationTime + '}';
	}

}
