package wf.garnier.feedback;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests((auth) -> {
			auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
			auth.requestMatchers("/").permitAll();
			auth.requestMatchers("/css/*").permitAll();
			auth.requestMatchers("/favicon.ico").permitAll();
			auth.anyRequest().authenticated();
		}).build();
	}

}
