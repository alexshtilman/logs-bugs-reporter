/**
 * 
 */
package telran.logs.bugs.configuration.components;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Alex Shtilman Apr 5, 2021
 *
 */
@Component
public class JwtUtilService {

	private static final String ROLES = "ROLES";

	@Value("${app-token-secret:secret}")
	String secret;

	@Value("${app-token-expiration-hour:1}")
	long expirationPeriod;

	public String generateToken(String username, String[] roles) {
		HashMap<String, Object> claims = new HashMap<>();
		claims.put(ROLES, roles);
		Date current = new Date();
		return Jwts.builder().setClaims(claims).setSubject(username)
				.setExpiration(new Date(current.getTime() + expirationPeriod * 3600000)).setIssuedAt(current)
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	public String[] validateToken(String tocken) {
		List<String> list = Jwts.parser().setSigningKey(secret).parseClaimsJws(tocken).getBody().get(ROLES, List.class);
		return list.toArray(String[]::new);
	}
}
