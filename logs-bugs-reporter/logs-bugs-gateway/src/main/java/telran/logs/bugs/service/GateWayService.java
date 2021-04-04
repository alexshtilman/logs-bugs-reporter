/**
 * 
 */
package telran.logs.bugs.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

/**
 * @author Alex Shtilman Apr 2, 2021
 *
 */
@Service
@Log4j2
public class GateWayService {
	@Value("${allowed-services:reporter-back-office:8080,info-back-office:8081}")
	List<String> allowedServices;

	@Value("${app-localhost:false}")
	boolean isLocalhost;

	HashMap<String, String> serviceToUrl;

	public String getPorxiedUri(ServerHttpRequest request) {
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
