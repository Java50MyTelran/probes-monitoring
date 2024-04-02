package telran.probes.controller;

import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import telran.probes.service.ProxyService;

@RestController
@RequiredArgsConstructor
public class GatewayController {
	final ProxyService proxyService;
	@GetMapping("/**")
	ResponseEntity<byte[]> getProxy(ProxyExchange<byte[]> proxy,
			HttpServletRequest request) {
		return proxyService.proxyRouting(proxy, request, "GET");
		
	}
	@PostMapping("/**")
	ResponseEntity<byte[]> postProxy(ProxyExchange<byte[]> proxy,
			HttpServletRequest request) {
		return proxyService.proxyRouting(proxy, request, "POST");
		
	}
	@DeleteMapping("/**")
	ResponseEntity<byte[]> deleteProxy(ProxyExchange<byte[]> proxy,
			HttpServletRequest request) {
		return proxyService.proxyRouting(proxy, request, "DELETE");
		
	}
	@PutMapping("/**")
	ResponseEntity<byte[]> putProxy(ProxyExchange<byte[]> proxy,
			HttpServletRequest request) {
		return proxyService.proxyRouting(proxy, request, "PUT");
		
	}
}
