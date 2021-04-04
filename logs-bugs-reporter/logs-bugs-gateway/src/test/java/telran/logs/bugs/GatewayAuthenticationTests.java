
package telran.logs.bugs;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static telran.logs.bugs.api.Constants.BUGS_CONTROLLER;
import static telran.logs.bugs.api.Constants.SERIOSNESS_BUGS_COUNT;
import static telran.logs.bugs.configuration.Constants.REPORTER_BACK_OFFICE;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.service.UserDetailsRefreshService;

/**
 * @author Alex Shtilman Apr 2, 2021
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "spring.cloud.config.enabled=false" })

@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@Log4j2
class GatewayAuthenticationTests {
	private static final String ACCOUTING_PROVIDER_ADDRESS = "reporter-back-office:8080";

	@Autowired
	WebTestClient testClient;

	@Value("${app-username-admin}")
	String adminUsername;
	@Value("${app-password-admin}")
	String adminPassword;

	@MockBean
	UserDetailsRefreshService userService;

	@MockBean
	ConcurrentHashMap<String, UserDetails> users;

	@Test
	@WithMockUser(username = "developer", password = "developer1234.com")
	@SneakyThrows
	void perforDevelpersQuerys() {

		when(users.get(anyString())).thenReturn(new User("developer", "developer1234.com",
				AuthorityUtils.createAuthorityList(new String[] { "DEVELOPER" })));

		testClient.get().uri(REPORTER_BACK_OFFICE + BUGS_CONTROLLER + SERIOSNESS_BUGS_COUNT).exchange().expectStatus()
				.isNotFound();
	}

}
