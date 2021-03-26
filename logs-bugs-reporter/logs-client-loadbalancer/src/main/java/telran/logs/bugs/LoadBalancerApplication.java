package telran.logs.bugs;

import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

/**
 * @author Alex Shtilman Mar 26, 2021
 *
 */
@SpringBootApplication
@RestController
@Log4j2
public class LoadBalancerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoadBalancerApplication.class, args);
	}

	ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory;

	public LoadBalancerApplication(ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory) {
		this.loadBalancerFactory = loadBalancerFactory;
	}

	@GetMapping("/")
	public String getUriByServiceName(@RequestParam(name = "service") String serviceName) {
		log.debug("serviceName: {}", serviceName);
		ReactiveLoadBalancer<ServiceInstance> rlb = loadBalancerFactory.getInstance(serviceName);
		log.debug("rlb: {}", rlb);
		Publisher<Response<ServiceInstance>> publisher = rlb.choose();
		log.debug("publisher: {}", publisher);
		Flux<Response<ServiceInstance>> chosen = Flux.from(publisher);
		log.debug("chosen: {}", chosen);
		ServiceInstance instance = chosen.blockFirst().getServer();
		log.debug("LoadBalancer resolved request of service url {} to {}", serviceName, instance.getUri().toString());
		return instance.getUri().toString();
	}

}
