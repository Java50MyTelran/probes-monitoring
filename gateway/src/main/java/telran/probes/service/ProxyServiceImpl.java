package telran.probes.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class ProxyServiceImpl implements ProxyService {
	@Value("#{${app.map.hosts.ports}}")
	Map<String, String> routingMap;
	@Override
	public ResponseEntity<byte[]> proxyRouting(ProxyExchange<byte[]> proxy, HttpServletRequest request,
			String httpMethod) {
		String routedUrl = getRoutedUrl(request);
		log.debug("routed URL is {}", routedUrl);
		ResponseEntity<byte[]> res = switch(httpMethod) {
		case "GET" -> proxy.uri(routedUrl).get();
		case "POST" -> proxy.uri(routedUrl).post();
		case "DELETE" -> proxy.uri(routedUrl).delete();
		case "PUT" -> proxy.uri(routedUrl).put();
		default -> throw new IllegalArgumentException("Unexpected value: " + httpMethod);
		};
		return res;
	}
	private String getRoutedUrl(HttpServletRequest request) {
		String resourceName = request.getRequestURI(); // example: /range/sensor/123
		log.debug("resource name: {}", resourceName);
		String firstPart = resourceName.split("/")[1]; // example: range
		log.debug("first name part is {}", firstPart);
		String hostPort = routingMap.get(firstPart);
		
		return String.format("http://%s%s", hostPort, resourceName);
	}
	@PostConstruct
	void logRoutingMap() {
		log.debug("routing map is {}", routingMap);
	}

}
