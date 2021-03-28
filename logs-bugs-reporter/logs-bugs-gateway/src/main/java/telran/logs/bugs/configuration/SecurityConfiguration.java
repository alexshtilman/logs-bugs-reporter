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

	public static final String BUGS_CONTROLLER = "/bugs";
	public static final String ARTIFACTS = "/artifacts";
	public static final String PROGRAMMERS = "/programmers";
	public static final String CLOSE = "/close";
	public static final String ASSIGN = "/assign";
	public static final String ANY = "**";
	public static final String OPEN = "/open";
	public static final String LOGS_CONTROLLER = "/logs";
	public static final String STATISTICS_CONTROLLER = "/statistics";

	public static final String REPORTER_BACK_OFFICE = "reporter-back-office";
	public static final String INFO_BACK_OFFICE = "info-back-office:8081";

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
				.authorizeExchange().pathMatchers(INFO_BACK_OFFICE + LOGS_CONTROLLER + ANY).hasRole("DEVELOPER")
				.pathMatchers(INFO_BACK_OFFICE + STATISTICS_CONTROLLER + ANY).hasRole("DEVELOPER")
				.pathMatchers(HttpMethod.POST, BUGS_CONTROLLER + OPEN).hasAnyRole("TESTER", "ASSIGNER", "DEVELOPER")
				.pathMatchers(HttpMethod.POST, OPEN + ASSIGN).hasAnyRole("TESTER", "ASSIGNER", "DEVELOPER")

				.pathMatchers(HttpMethod.PUT, REPORTER_BACK_OFFICE + BUGS_CONTROLLER + ASSIGN).hasRole("ASSIGNER")
				.pathMatchers(HttpMethod.PUT, REPORTER_BACK_OFFICE + BUGS_CONTROLLER + CLOSE).hasRole("TESTER")
				.pathMatchers(HttpMethod.POST, REPORTER_BACK_OFFICE + BUGS_CONTROLLER + PROGRAMMERS)
				.hasRole("PROJECT_OWNER")
				.pathMatchers(HttpMethod.POST, REPORTER_BACK_OFFICE + BUGS_CONTROLLER + ARTIFACTS)
				.hasAnyRole("TEAM_LEAD", "ASSIGNER")
				.pathMatchers(HttpMethod.GET, REPORTER_BACK_OFFICE + BUGS_CONTROLLER + ANY).authenticated().and()
				.build();
		return securityFiltersChain;
	}

	@PostConstruct
	void updateMapUserDetails() throws InterruptedException {
		refreshService.start();

	}
}
