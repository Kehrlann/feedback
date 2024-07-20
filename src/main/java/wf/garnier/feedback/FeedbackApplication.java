package wf.garnier.feedback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(FeedbackProperties.class)
public class FeedbackApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedbackApplication.class, args);
	}

}
