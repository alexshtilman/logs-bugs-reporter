package telran.security.accounting.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

	@Value("${default-admin-pwd}")
	String adminPwd;
	@Value("${default-user-pwd}")
	String userPwd;

	@Value("${app-security-enable:true}")
	boolean securityEnable;

	@Bean
	PasswordEncoder getPasswordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}


	/*
	 * @Bean MapReactiveUserDetailsService getMapDetails() { // noop means a plain
	 * text UserDetails user = new User("user", "{noop}" + userPwd,
	 * AuthorityUtils.createAuthorityList("ROLE_USER")); UserDetails admin = new
	 * User("admin", "{noop}" + adminPwd,
	 * AuthorityUtils.createAuthorityList("ROLE_ADMIN")); return new
	 * MapReactiveUserDetailsService(Arrays.asList(user, admin));
	 * 
	 * }
	 */

	@Bean
	SecurityWebFilterChain securityFiltersChain(ServerHttpSecurity httpSecurity) {
		if (!securityEnable) {
			return httpSecurity.csrf().disable().authorizeExchange().anyExchange().permitAll().and().build();
		}

		return httpSecurity.csrf().disable().httpBasic().and().authorizeExchange()
				.pathMatchers(HttpMethod.GET, "/account/**").hasAnyRole("USER", "ADMIN").pathMatchers("/**")
				.hasRole("ADMIN").and().build();

	}
}