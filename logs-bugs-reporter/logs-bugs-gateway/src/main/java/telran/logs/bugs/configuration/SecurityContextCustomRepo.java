/**
 * 
 */
package telran.logs.bugs.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import telran.logs.bugs.configuration.components.AuthenticationCustomManager;

/**
 * @author Alex Shtilman Apr 5, 2021
 *
 */
@Component
public class SecurityContextCustomRepo implements ServerSecurityContextRepository {

	@Autowired
	AuthenticationCustomManager authenticator;

	@Override
	public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
		// unsupported
		return null;
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer")) {
			return Mono.empty();
		}
		String authTocken = authHeader.substring(7);

		Mono<SecurityContext> result = authenticator
				.authenticate(new UsernamePasswordAuthenticationToken(authTocken, authTocken))
				.map(SecurityContextImpl::new);

		return result;
	}
}
