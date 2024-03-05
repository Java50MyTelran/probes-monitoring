package telran.probes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

@Configuration
@Getter
public class ServiceConfiguration {
@Value("${app.emails.provider.host:localhost}")
	String host;
@Value("${app.emails.provider.port:8585}")
int port;
@Value("${app.emails.provider.path:/emails/sensor/}")
String path;
@Bean
RestTemplate getRestTemplate() {
	return new RestTemplate();
}
}
