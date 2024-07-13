package wf.garnier.feedback;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
class EmulatorConfiguration {

	// By default, autoconfiguration will initialize application default credentials.
	// For testing purposes, don't use any credentials. Bootstrap w/
	// NoCredentialsProvider.
	@Bean
	CredentialsProvider googleCredentials() {
		return NoCredentialsProvider.create();
	}

}
