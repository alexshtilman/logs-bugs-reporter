/**
 * 
 */
package telran.logs.bugs.configuration.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * @author Alex Shtilman Apr 5, 2021
 *
 */
@Component
public class AuthenticationCustomManager implements ReactiveAuthenticationManager {

	@Autowired
	JwtUtilService jwtUtilService;

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String authTocken = authentication.getCredentials().toString();
		String[] roles = jwtUtilService.validateToken(authTocken);
		UsernamePasswordAuthenticationToken tokenObj = new UsernamePasswordAuthenticationToken(null, null,
				AuthorityUtils.createAuthorityList(roles));
		return Mono.just(tokenObj);
	}

}
