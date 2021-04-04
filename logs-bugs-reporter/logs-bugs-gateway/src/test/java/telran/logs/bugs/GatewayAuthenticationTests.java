
package telran.logs.bugs;

import static org.mockito.Mockito.doAnswer;
import static telran.logs.bugs.api.Constants.BUGS_CONTROLLER;
import static telran.logs.bugs.configuration.Constants.REPORTER_BACK_OFFICE;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.service.UserDetailsRefreshService;

/**
 * @author Alex Shtilman Apr 2, 2021
 *
 */
@SpringBootTest(properties = { "spring.cloud.config.enabled=false" })

@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
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

	@BeforeAll
	@SneakyThrows
	void init() {
		doAnswer(invocation -> {
			users.put("developer", new User("developer", "{noop}developer123456789.com",
					AuthorityUtils.createAuthorityList(new String[] { "DEVELOPER" })));
			users.put("assigner", new User("assigner", "{noop}assigner123456789.com",
					AuthorityUtils.createAuthorityList(new String[] { "ASSIGNER" })));
			users.put("tester", new User("tester", "{noop}tester123456789.com",
					AuthorityUtils.createAuthorityList(new String[] { "TESTER" })));
			users.put("product-owner", new User("product-owner", "{noop}product-owner123456789.com",
					AuthorityUtils.createAuthorityList(new String[] { "PRODUCT_OWNER" })));
			users.put("team-leader", new User("team-leader", "{noop}team-leader123456789.com",
					AuthorityUtils.createAuthorityList(new String[] { "TEAM_LEADER" })));
			return null;
		}).when(userService).run();
		userService.run();

	}

	@Test
	@Order(2)
	void performAllowedForAllAuthorizedUsers() {
		List<String> users = new ArrayList<>();
		users.add(getAuthorizationTocken("developer", "developer123456789.com"));
		users.add(getAuthorizationTocken("assigner", "assigner123456789.com"));
		users.add(getAuthorizationTocken("tester", "tester123456789.com"));
		users.add(getAuthorizationTocken("product-owner", "product-owner123456789.com"));
		users.add(getAuthorizationTocken("team-leader", "team-leader123456789.com"));

		users.forEach(user -> {
			testClient.get().uri(BUGS + ANY_QUERY).header("Authorization", user).exchange().expectStatus().isNotFound();
		});

	}

	private static String getAuthorizationTocken(String username, String password) {
		String rowText = username + ":" + password;
		return "Basic " + Base64.getEncoder().encodeToString(rowText.getBytes());
	}
}
