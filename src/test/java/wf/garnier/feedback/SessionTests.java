package wf.garnier.feedback;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTests {

	@Test
	void constructor() {
		var beforeCreation = LocalDateTime.now(ZoneId.of("UTC"));
		var session = new Session("session name");
		var afterCreation = LocalDateTime.now(ZoneId.of("UTC"));

		assertThat(session.getName()).isEqualTo("session name");
		assertThat(session.getActive()).isTrue();
		assertThat(session.getSessionId()).isNotNull();
		assertThat(session.getCreationTime()).isBetween(beforeCreation, afterCreation);
		//@formatter:off
		assertThat(session.getFeedbackChoices()).containsExactly(
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
	}

}
