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

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

/**
 * @author Alex Shtilman Apr 5, 2021
 *
 */
@Component
@Log4j2
public class AuthenticationCustomManager implements ReactiveAuthenticationManager {

	@Autowired
	JwtUtilService jwtUtilService;

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String authTocken = authentication.getCredentials().toString();

		String[] roles = {};
		try {
			roles = jwtUtilService.validateToken(authTocken);
		} catch (MalformedJwtException e) {
			log.debug("incorrect tocken!");
			return Mono.empty();
		} catch (ExpiredJwtException e) {
			log.debug("expired tocken!");
			return Mono.empty();
		} catch (SignatureException e) {
			log.debug("wrong signature!");
			return Mono.empty();
		}
		UsernamePasswordAuthenticationToken tokenObj = new UsernamePasswordAuthenticationToken(null, null,
				AuthorityUtils.createAuthorityList(roles));
		return Mono.just(tokenObj);
	}

}
