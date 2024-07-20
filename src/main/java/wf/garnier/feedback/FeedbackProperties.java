package wf.garnier.feedback;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "feedback")
@Validated
public record FeedbackProperties(@NotNull @NotEmpty List<String> admin) {

}
