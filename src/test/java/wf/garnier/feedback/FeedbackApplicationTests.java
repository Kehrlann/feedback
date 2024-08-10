package wf.garnier.feedback;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeedbackApplicationTests extends TestBase {

	@Test
	void listsSessions() throws Exception {
		HtmlPage htmlPage = webClient.getPage("/");

		var sessions = htmlPage.querySelectorAll("li").stream().map(DomNode::getTextContent).toList();

		assertThat(sessions).containsExactly("Other test session", "Test session");
	}

}
