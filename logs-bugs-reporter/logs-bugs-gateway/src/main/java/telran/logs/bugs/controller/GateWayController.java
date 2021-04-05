/**
 * 
 */
package telran.logs.bugs.controller;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import telran.logs.bugs.configuration.components.JwtUtilService;
import telran.logs.bugs.service.GateWayService;

/**
 * @author Alex Shtilman Mar 25, 2021
 *
 */
@RestController
public class GateWayController {

	@Autowired
	GateWayService gateWayService;

	@Autowired
	JwtUtilService jwtUtilService;
	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	ConcurrentHashMap<String, UserDetails> users;

	static class AuthData {
		public String username;
		public String password;
	}

	@PostMapping("/login")
	Mono<ResponseEntity<String>> login(@RequestBody AuthData authData) {
		UserDetails details = users.get(authData.username);
		if (details == null || !passwordEncoder.matches(authData.password, details.getPassword())) {
			return Mono.just(ResponseEntity.badRequest().body("wrong credentionals"));
		}
		return Mono.just(ResponseEntity.ok().body(jwtUtilService.generateToken(details.getUsername(),
				details.getAuthorities().stream().map(auth -> auth.getAuthority()).toArray(String[]::new))));
	}

	@PostMapping("/**")
	public Mono<ResponseEntity<byte[]>> proxyPostRequests(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		return gateWayService.proxyRun(proxy, request, HttpMethod.POST);
	}

	@GetMapping("/**")
	public Mono<ResponseEntity<byte[]>> proxyGetRequests(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		return gateWayService.proxyRun(proxy, request, HttpMethod.GET);
	}

	@PutMapping("/**")
	public Mono<ResponseEntity<byte[]>> proxyPutRequests(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		return gateWayService.proxyRun(proxy, request, HttpMethod.PUT);
	}

	@DeleteMapping("/**")
	public Mono<ResponseEntity<byte[]>> proxyDeleteRequests(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		return gateWayService.proxyRun(proxy, request, HttpMethod.DELETE);
	}

}
