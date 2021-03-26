/**
 * 
 */
package telran.logs.bugs.controller;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
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

/**
 * @author Alex Shtilman Mar 25, 2021
 *
 */
@RestController
@Log4j2
public class GateWayController {
	@Value("${allowed-services:reporter-back-office:8080,info-back-office:8081}")
	List<String> allowedServices;

	@Value("${app-localhost:false}")
	boolean isLocalhost;

	HashMap<String, String> serviceToUrl;

	@PostMapping("/**")
	public Mono<ResponseEntity<byte[]>> proxyPostRequests(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		String uri = getPorxiedUri(request);
		if (uri == null) {
			return Mono.just(ResponseEntity.status(404).body("service not found".getBytes()));
		}
		return proxy.uri(uri).post();
	}

	@GetMapping("/**")
	public Mono<ResponseEntity<byte[]>> proxyGetRequests(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		String uri = getPorxiedUri(request);
		if (uri == null) {
			return Mono.just(ResponseEntity.status(404).body("service not found".getBytes()));
		}
		return proxy.uri(uri).get();
	}

	@PutMapping("/**")
	public Mono<ResponseEntity<byte[]>> proxyPutRequests(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		String uri = getPorxiedUri(request);
		if (uri == null) {
			return Mono.just(ResponseEntity.status(404).body("service not found".getBytes()));
		}
		return proxy.uri(uri).put();
	}

	@DeleteMapping("/**")
	public Mono<ResponseEntity<byte[]>> proxyDeleteRequests(ProxyExchange<byte[]> proxy, ServerHttpRequest request) {
		String uri = getPorxiedUri(request);
		if (uri == null) {
			return Mono.just(ResponseEntity.status(404).body("service not found".getBytes()));
		}
		return proxy.uri(uri).delete();
	}

	private String getPorxiedUri(ServerHttpRequest request) {
		String uri = request.getURI().toString();
		String serviceName = uri.split("/+")[2];
		log.debug("getUrl: {} ", serviceName);
		String endpoint = serviceToUrl.get(serviceName);
		if (endpoint != null) {
			int indService = uri.indexOf(serviceName) + serviceName.length();
			endpoint += uri.substring(indService);
			log.debug("result uri: {}", endpoint);
		}
		return endpoint;
	}

	@PostConstruct
	void fillServiceToUrl() {
		serviceToUrl = new HashMap<>();
		allowedServices.forEach(service -> {
			String[] token = service.split(":");
			serviceToUrl.put(token[0], String.format("http://%s:%s", isLocalhost ? "localhost" : token[0], token[1]));
		});
		log.debug("allowedServices : {}", allowedServices);
	}
}
