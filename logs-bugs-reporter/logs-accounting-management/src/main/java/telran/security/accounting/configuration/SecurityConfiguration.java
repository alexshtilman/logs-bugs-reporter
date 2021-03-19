package telran.security.accounting.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfiguration {

	@Value("${default-admin-pwd}")
	String adminPwd;
	@Value("${default-user-pwd}")
	String userPwd;

	@Bean
	MapReactiveUserDetailsService getMapDetails() {
		// noop means a plain text
		UserDetails user = new User("user", "{noop}" + userPwd, AuthorityUtils.createAuthorityList("ROLE_USER"));
		UserDetails admin = new User("admin", "{noop}" + adminPwd, AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
		return new MapReactiveUserDetailsService(Arrays.asList(user, admin));

	}

	@Bean
	SecurityWebFilterChain securityFiltersChain(ServerHttpSecurity httpSecurity) {

		SecurityWebFilterChain securityFiltersChain = httpSecurity.csrf().disable().httpBasic().and()
				.authorizeExchange()
				.pathMatchers(HttpMethod.GET, "/account/**").hasAnyRole("USER", "ADMIN")
				.pathMatchers("/**").hasRole("ADMIN")
				.and().build();
		return securityFiltersChain;
	}
}