package telran.security.accounting.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfiguration {

	@Value("${default-user-pwd}")
	String passwordUser;
	@Value("${default-admin-pwd}")
	String passwordAdmin;
	@Value("${app-security-enable:true}")
	boolean securityEnable;

	@Bean
	MapReactiveUserDetailsService getMapDetails() {
		// noop means a plain text

		UserDetails user = new User("user", "{noop}" + passwordUser, AuthorityUtils.createAuthorityList("ROLE_USER"));
		UserDetails admin = new User("admin", "{noop}" + passwordAdmin,
				AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
		UserDetails users[] = { user, admin };
		return new MapReactiveUserDetailsService(users);

	}

	@Bean
	PasswordEncoder getPasswordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	SecurityWebFilterChain securityFiltersChain(ServerHttpSecurity httpSecurity) {
		if (!securityEnable) {
			return httpSecurity.csrf().disable().authorizeExchange().anyExchange().permitAll().and().build();
		}
		SecurityWebFilterChain securityFiltersChain = httpSecurity.csrf().disable().httpBasic().and()
				.authorizeExchange().pathMatchers(HttpMethod.GET).hasAnyRole("USER", "ADMIN").anyExchange()
				.hasRole("ADMIN").and().build();
		return securityFiltersChain;
	}
}
