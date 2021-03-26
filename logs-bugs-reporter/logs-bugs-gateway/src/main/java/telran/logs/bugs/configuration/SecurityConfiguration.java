/**
 * 
 */
package telran.logs.bugs.configuration;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author Alex Shtilman Mar 27, 2021
 *
 */
@Configuration
public class SecurityConfiguration {

	@Autowired
	UserDetailsRefreshService refreshService;
	@Autowired
	ConcurrentHashMap<String, UserDetails> users;

	@Bean
	MapReactiveUserDetailsService getMapDetails() {
		return new MapReactiveUserDetailsService(users);
	}

	@Bean
	SecurityWebFilterChain securityFiltersChain(ServerHttpSecurity httpSecurity) {
		SecurityWebFilterChain securityFiltersChain = httpSecurity.csrf().disable().httpBasic().and()
				.authorizeExchange().pathMatchers(HttpMethod.GET).hasRole("GETTER").pathMatchers(HttpMethod.POST)
				.hasRole("POSTER").and().build();
		return securityFiltersChain;
	}

	@PostConstruct
	void updateMapUserDetails() throws InterruptedException {
		refreshService.start();

	}
}
