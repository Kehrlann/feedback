package wf.garnier.feedback;

import java.io.IOException;
import java.util.Collections;

import com.gargoylesoftware.htmlunit.WebClient;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(EmulatorConfiguration.class)
class TestBase {

	@Autowired
	protected WebClient webClient;

	@Autowired
	protected MockMvc mvc;

	@Autowired
	protected SessionRepository sessionRepository;

	@Autowired
	protected LocalDatastoreHelper datastoreHelper;

	@BeforeEach
	void setUp() throws IOException {
		datastoreHelper.reset();
		sessionRepository.save(new Session("Test session", "Some Conference, 2024-04-22"));
		sessionRepository.save(new Session("Other test session", "SpringIO, 2024-05-30"));
		sessionRepository.save(new Session("Inactive session", "MiXiT, 2024-04-29", false, Collections.emptyList()));
	}

}
