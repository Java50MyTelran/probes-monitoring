package telran.probes.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SecurityConfiguration {
	
	@Value("${app.user.notifier.role}")
	String userNotifierRole;
	@Value("${app.user.range.role}")
	String userRangeRole;
	@Value("${app.admin.notifier.role}")
	String adminNotifierRole;
	@Value("${app.user.accounts.role}")
	String userAccountsRole;
	@Value("${app.admin.range.role}")
	String adminRangeRole;
	
	@Value("${app.sensor.range.provider.url}")
	String rangeSensorUrl;
	@Value("${app.emails.provider.url}")
	String emailsSensorUrl;
	@Value("${app.accounts.provider.url}")
	String accountsUrl;
	@Value("${app.admin.range.url}")
	String adminRangeUrl;
	@Value("${app.admin.emails.url}")
	String adminEmailsUrl;
@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.cors(customizer -> customizer.disable());
		httpSecurity.csrf(customizer -> customizer.disable());
		httpSecurity.authorizeHttpRequests(customizer -> customizer
				.requestMatchers(adminRangeUrl + "/**").hasRole(adminRangeRole)
				.requestMatchers(adminEmailsUrl + "/**").hasRole(adminNotifierRole)
				.requestMatchers(rangeSensorUrl + "/**").hasRole(userRangeRole)
				.requestMatchers(emailsSensorUrl + "/**").hasRole(userNotifierRole)
				.requestMatchers(accountsUrl + "/**").hasRole(userAccountsRole));
		httpSecurity.sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));
		httpSecurity.httpBasic(Customizer.withDefaults());
		return httpSecurity.build();
	}
}
