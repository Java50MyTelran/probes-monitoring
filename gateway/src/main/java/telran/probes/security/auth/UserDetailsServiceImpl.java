package telran.probes.security.auth;

import java.net.URI;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.AccountDto;
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
	@Value("${app.accounts.provider.host}")
	String host;
	@Value("${app.accounts.provider.port}")
	int port;
	@Value("${app.accounts.provider.url}")
	String url;
	final RestTemplate restTemplate;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("received username is {}", username);
		String fullUrl = getUrl(username);
		log.debug("full url for getting account is {}", fullUrl);
		ResponseEntity<?> response = restTemplate.exchange(fullUrl,
				HttpMethod.GET, null, AccountDto.class);
		if(response.getStatusCode().is4xxClientError()) {
			throw new UsernameNotFoundException(username);
		}
		AccountDto account = (AccountDto) response.getBody();
		String[] roles = Arrays.stream(account.roles()).map(r -> "ROLE_" + r).toArray(String[]::new);
		return new User(username, account.password(), AuthorityUtils.createAuthorityList(roles));
	}
	private String getUrl(String username) {
		return String.format("http://%s:%d%s/%s", host, port, url,username);
	}

}
