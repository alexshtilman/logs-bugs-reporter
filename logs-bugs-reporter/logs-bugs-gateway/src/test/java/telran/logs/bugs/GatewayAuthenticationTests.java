
package telran.logs.bugs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static telran.logs.bugs.api.Constants.BUGS_CONTROLLER;
import static telran.logs.bugs.configuration.Constants.REPORTER_BACK_OFFICE;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import telran.logs.bugs.service.GateWayService;
import telran.logs.bugs.service.UserDetailsRefreshService;

/**
 * @author Alex Shtilman Apr 2, 2021
 *
 */
@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
class GatewayAuthenticationTests {

	private static final String ANY_QUERY = "/any_query?any_param=123";
	private static final String BUGS = REPORTER_BACK_OFFICE + BUGS_CONTROLLER;

	@Autowired
	WebTestClient testClient;

	@MockBean
	UserDetailsRefreshService userService;

	@MockBean
	ConcurrentHashMap<String, UserDetails> users;

	@MockBean
	RestTemplate mockRestTemplate;

	@MockBean
	GateWayService gateWayService;

	@BeforeEach
	void init() {
		doNothing().when(userService).start();
		when(gateWayService.proxyRun(any(), any(), any(HttpMethod.class)))
				.thenReturn(Mono.just(ResponseEntity.ok("ok".getBytes())));

		users.putIfAbsent("developer", new User("developer", "{noop}developer123456789.com",
				AuthorityUtils.createAuthorityList("ROLE_DEVELOPER")));
		users.putIfAbsent("assigner", new User("assigner", "{noop}assigner123456789.com",
				AuthorityUtils.createAuthorityList("ROLE_ASSIGNER")));
		users.putIfAbsent("tester",
				new User("tester", "{noop}tester123456789.com", AuthorityUtils.createAuthorityList("ROLE_TESTER")));
		users.putIfAbsent("product-owner", new User("product-owner", "{noop}product-owner123456789.com",
				AuthorityUtils.createAuthorityList("ROLE_PRODUCT_OWNER")));
		users.putIfAbsent("team-leader", new User("team-leader", "{noop}team-leader123456789.com",
				AuthorityUtils.createAuthorityList("ROLE_TEAM_LEADER")));

	}

	@Test
	void performAllowedForAllAuthorizedUsers() {
		List<String> users = new ArrayList<>();
		users.add(getAuthorizationTocken("developer", "developer123456789.com"));
		users.add(getAuthorizationTocken("assigner", "assigner123456789.com"));
		users.add(getAuthorizationTocken("tester", "tester123456789.com"));
		users.add(getAuthorizationTocken("product-owner", "product-owner123456789.com"));
		users.add(getAuthorizationTocken("team-leader", "team-leader123456789.com"));

		users.forEach(user -> {
			testClient.get().uri(BUGS + ANY_QUERY).header("Authorization", user).exchange().expectStatus().isOk();
		});

	}

	private static String getAuthorizationTocken(String username, String password) {
		String rowText = username + ":" + password;
		return "Basic " + Base64.getEncoder().encodeToString(rowText.getBytes());
	}
}
