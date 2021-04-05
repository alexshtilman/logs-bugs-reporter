/**
 * 
 */
package telran.logs.bugs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import telran.logs.bugs.service.GateWayService;

/**
 * @author Alex Shtilman Mar 25, 2021
 *
 */
@RestController
public class GateWayController {

	@Autowired
	GateWayService gateWayService;

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
