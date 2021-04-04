/**
 * 
 */
package telran.logs.bugs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import telran.logs.bugs.service.GateWayService;

/**
 * @author Alex Shtilman Mar 25, 2021
 *
 */
@RestController
@Log4j2
public class GateWayController {
	private static final String SERVICE_NOT_FOUND = "service not found";

	@Autowired
	GateWayService gateWayService;

	@PostMapping("/**")
	public Mono<ResponseEntity<byte[]>> proxyPostRequests(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		String uri = gateWayService.getPorxiedUri(request);
		if (uri == null) {
			log.debug("Service {} not found!", request);
			return Mono.just(ResponseEntity.status(404).body(SERVICE_NOT_FOUND.getBytes()));
		}
		return proxy.uri(uri).post();
	}

	@GetMapping("/**")
	public Mono<ResponseEntity<byte[]>> proxyGetRequests(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		String uri = gateWayService.getPorxiedUri(request);
		if (uri == null) {
			log.debug("Service {} not found!", request);
			return Mono.just(ResponseEntity.status(404).body(SERVICE_NOT_FOUND.getBytes()));
		}
		return proxy.uri(uri).get();
	}

	@PutMapping("/**")
	public Mono<ResponseEntity<byte[]>> proxyPutRequests(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		String uri = gateWayService.getPorxiedUri(request);
		if (uri == null) {
			return Mono.just(ResponseEntity.status(404).body(SERVICE_NOT_FOUND.getBytes()));
		}
		return proxy.uri(uri).put();
	}

	@DeleteMapping("/**")
	public Mono<ResponseEntity<byte[]>> proxyDeleteRequests(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		String uri = gateWayService.getPorxiedUri(request);
		if (uri == null) {
			return Mono.just(ResponseEntity.status(404).body(SERVICE_NOT_FOUND.getBytes()));
		}
		return proxy.uri(uri).delete();
	}

}
