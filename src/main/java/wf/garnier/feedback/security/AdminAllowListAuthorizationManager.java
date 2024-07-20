package wf.garnier.feedback.security;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import wf.garnier.feedback.FeedbackProperties;

@Component
class AdminAllowListAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

	private final List<String> adminEmail;

	AdminAllowListAuthorizationManager(FeedbackProperties properties) {
		this.adminEmail = properties.admin();
	}

	@Override
	public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
		return new AuthorizationDecision(this.adminEmail.contains(authentication.get().getName()));
	}

}
