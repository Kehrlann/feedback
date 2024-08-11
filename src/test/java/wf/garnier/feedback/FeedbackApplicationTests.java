package wf.garnier.feedback;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeedbackApplicationTests extends TestBase {

	@Test
	void listsSessions() throws Exception {
		HtmlPage htmlPage = webClient.getPage("/");

		var sessions = htmlPage.querySelectorAll("li").stream().map(DomNode::getTextContent).map(String::trim).toList();

		assertThat(sessions).containsExactly("Other test session", "Test session");
	}

	@Test
	void viewSession() throws Exception {
		HtmlPage htmlPage = webClient.getPage("/");

		htmlPage = ((HtmlAnchor) htmlPage.querySelector("li > a")).click();

		var sessionTitle = htmlPage.querySelector("h1").getTextContent();
		assertThat(sessionTitle).isEqualTo("Other test session");
	}

}
