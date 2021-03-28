package telran.logs.bugs;

import org.reactivestreams.Publisher;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Component
@Log4j2
public class LoadBalancerComponent {
	ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory;

	public LoadBalancerComponent(ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory) {
		this.loadBalancerFactory = loadBalancerFactory;
	}

	public String getBaseUrl(String serviceName) {
		ReactiveLoadBalancer<ServiceInstance> rlb = loadBalancerFactory.getInstance(serviceName);
		Publisher<Response<ServiceInstance>> publisher = rlb.choose();
		Flux<Response<ServiceInstance>> chosen = Flux.from(publisher);
		ServiceInstance instance = chosen.blockFirst().getServer();
		log.debug("LoadBalancer resolved request of service url {} to {}", serviceName, instance.getUri().toString());
		return instance.getUri().toString();
	}
}
