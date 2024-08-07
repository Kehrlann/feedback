package wf.garnier.feedback.security;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, AdminAllowListAuthorizationManager authorizationManager)
			throws Exception {
		return http.authorizeHttpRequests((auth) -> {
			auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
			auth.requestMatchers("/").permitAll();
			auth.requestMatchers("/session/**").permitAll();
			auth.requestMatchers("/css/*").permitAll();
			auth.requestMatchers("/favicon.ico").permitAll();
			auth.requestMatchers("/admin/**").access(authorizationManager);
			auth.anyRequest().denyAll();
		}).oauth2Login(Customizer.withDefaults()).build();
	}

}
