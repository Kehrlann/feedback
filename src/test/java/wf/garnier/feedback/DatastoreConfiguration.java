package wf.garnier.feedback;

import com.google.cloud.NoCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.DatastoreEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Container
	public static DatastoreEmulatorContainer DATASTORE = new DatastoreEmulatorContainer(
			DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:441.0.0-emulators"));

	static Datastore getDatastore() {
		var emulator = DATASTORE;
		DatastoreOptions options = DatastoreOptions.newBuilder()
			.setHost(emulator.getEmulatorEndpoint())
			.setCredentials(NoCredentials.getInstance())
			.setRetrySettings(ServiceOptions.getNoRetrySettings())
			.setProjectId(emulator.getProjectId())
			.build();
		Datastore datastore = options.getService();
		return datastore;
	}
}
