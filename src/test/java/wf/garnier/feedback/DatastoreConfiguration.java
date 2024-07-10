package wf.garnier.feedback;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.DatastoreEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@TestConfiguration(proxyBeanMethods = false)
class DatastoreConfiguration {

	@Container
	public static DatastoreEmulatorContainer DATASTORE = new DatastoreEmulatorContainer(
			DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:441.0.0-emulators"))
		.withFlags("--no-store-on-disk");

	static void reset() {
		var client = RestClient.create();
		var resetStatus = client.post()
			.uri("http://" + DATASTORE.getEmulatorEndpoint() + "/reset")
			.retrieve()
			.toBodilessEntity()
			.getStatusCode();
		assertThat(resetStatus).describedAs("/reset call on the emulator should return 200").isEqualTo(HttpStatus.OK);
	}

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.cloud.gcp.datastore.host",
				() -> "http://" + DATASTORE.getHost() + ":" + DATASTORE.getFirstMappedPort());
	}

}
