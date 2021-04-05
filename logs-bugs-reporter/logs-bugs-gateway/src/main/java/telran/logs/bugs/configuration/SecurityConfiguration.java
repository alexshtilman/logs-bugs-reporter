/**
 * 
 */
package telran.logs.bugs.configuration;

import static telran.logs.bugs.api.Constants.ARTIFACTS;
import static telran.logs.bugs.api.Constants.BUGS_CONTROLLER;
import static telran.logs.bugs.api.Constants.CLOSE;
import static telran.logs.bugs.api.Constants.OPEN;
import static telran.logs.bugs.api.Constants.PROGRAMMERS;
import static telran.logs.bugs.configuration.Constants.ANY;
import static telran.logs.bugs.configuration.Constants.ASSIGNER;
import static telran.logs.bugs.configuration.Constants.DEVELOPER;
import static telran.logs.bugs.configuration.Constants.INFO_BACK_OFFICE;
import static telran.logs.bugs.configuration.Constants.PROJECT_OWNER;
import static telran.logs.bugs.configuration.Constants.REPORTER_BACK_OFFICE;
import static telran.logs.bugs.configuration.Constants.TEAM_LEAD;
import static telran.logs.bugs.configuration.Constants.TESTER;
import static telran.security.accounting.api.Constants.ASSIGN;

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

import telran.logs.bugs.service.UserDetailsRefreshService;

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
		String BUGS = REPORTER_BACK_OFFICE + BUGS_CONTROLLER;

		SecurityWebFilterChain securityFiltersChain = httpSecurity.csrf().disable().httpBasic().and()
				.authorizeExchange().pathMatchers(HttpMethod.GET, INFO_BACK_OFFICE + ANY).hasRole(DEVELOPER)
				.pathMatchers(HttpMethod.POST, BUGS + OPEN).hasAnyRole(TESTER, ASSIGNER, DEVELOPER)
				.pathMatchers(HttpMethod.POST, BUGS + OPEN + ASSIGN).hasAnyRole(TESTER, ASSIGNER, DEVELOPER)
				.pathMatchers(HttpMethod.PUT, BUGS + ASSIGN).hasRole(ASSIGNER)
				.pathMatchers(HttpMethod.PUT, BUGS + CLOSE).hasRole(TESTER)
				.pathMatchers(HttpMethod.POST, BUGS + PROGRAMMERS).hasRole(PROJECT_OWNER)
				.pathMatchers(HttpMethod.POST, BUGS + ARTIFACTS).hasAnyRole(TEAM_LEAD, ASSIGNER).anyExchange()
				.authenticated().and().build();
		return securityFiltersChain;
	}

	@PostConstruct
	void updateMapUserDetails() throws InterruptedException {
		refreshService.start();
	}
}
