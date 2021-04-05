package telran.logs.bugs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static telran.logs.bugs.api.Constants.ARTIFACTS;
import static telran.logs.bugs.api.Constants.BUGS_CONTROLLER;
import static telran.logs.bugs.api.Constants.CLOSE;
import static telran.logs.bugs.api.Constants.EMAIL_BUGS_COUNTS;
import static telran.logs.bugs.api.Constants.LOGS_CONTROLLER;
import static telran.logs.bugs.api.Constants.OPEN;
import static telran.logs.bugs.api.Constants.PROGRAMMERS;
import static telran.logs.bugs.api.Constants.STATISTICS_CONTROLLER;
import static telran.logs.bugs.configuration.Constants.INFO_BACK_OFFICE;
import static telran.logs.bugs.configuration.Constants.REPORTER_BACK_OFFICE;
import static telran.security.accounting.api.Constants.ASSIGN;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;
import telran.logs.bugs.service.GateWayService;
import telran.logs.bugs.service.UserDetailsRefreshService;

/**
 * @author Alex Shtilman Apr 2, 2021
 *
 */
@SpringBootTest(properties = { "spring.cloud.config.enabled=false" })
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GatewayAuthorizationTests {

	private static final String BUGS = REPORTER_BACK_OFFICE + BUGS_CONTROLLER;

	@Autowired
	WebTestClient testClient;

	@MockBean
	GateWayService gateWayService;

	@MockBean
	UserDetailsRefreshService userService;

	@Autowired
	ConcurrentHashMap<String, UserDetails> users;

	@BeforeEach
	void setup() {
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
			testClient.get().uri(BUGS + EMAIL_BUGS_COUNTS).header("Authorization", user).exchange().expectStatus()
					.isOk();
		});

	}

	// INFO_BACK_OFFICE + LOGS_CONTROLLER & STATISTICS_CONTROLLER only get
	@Test
	@WithMockUser(roles = { "DEVELOPER" })
	void perforDevelpersQuerys() {
		retriveInformationAboutLogs();
		openBug().isOk();
		openAndAssignBug().isOk();

		closeBug().isForbidden();
		addArtifact().isForbidden();
		addProgrammer().isForbidden();
		assignBug().isForbidden();
	}

	@Test
	@WithMockUser(roles = { "TESTER" })
	void perforTestersQuerys() {
		commonGetRequests();
		openBug().isOk();
		openAndAssignBug().isOk();
		closeBug().isOk();

		addArtifact().isForbidden();
		addProgrammer().isForbidden();
	}

	@Test
	@WithMockUser(roles = { "ASSIGNER" })
	void perforAssgnerQuerys() {
		commonGetRequests();
		openBug().isOk();
		openAndAssignBug().isOk();
		assignBug().isOk();
		addArtifact().isOk();

		closeBug().isForbidden();
		addProgrammer().isForbidden();
	}

	@Test
	@WithMockUser(roles = { "PROJECT_OWNER" })
	void perforProjectOwnerQuerys() {
		commonGetRequests();
		addProgrammer().isOk();

		closeBug().isForbidden();
		addArtifact().isForbidden();

		openBug().isForbidden();
		openAndAssignBug().isForbidden();
		assignBug().isForbidden();
	}

	@Test
	@WithMockUser(roles = { "TEAM_LEAD" })
	void perforTeamLeadQuerys() {
		commonGetRequests();
		addArtifact().isOk();

		closeBug().isForbidden();
		addProgrammer().isForbidden();

		openBug().isForbidden();
		openAndAssignBug().isForbidden();
		assignBug().isForbidden();
	}

	@Test
	void unauthUser() {
		testClient.get().uri(BUGS + EMAIL_BUGS_COUNTS).exchange().expectStatus().isUnauthorized();
		testClient.get().uri(INFO_BACK_OFFICE + LOGS_CONTROLLER + EMAIL_BUGS_COUNTS).exchange().expectStatus()
				.isUnauthorized();
		testClient.get().uri(INFO_BACK_OFFICE + STATISTICS_CONTROLLER + EMAIL_BUGS_COUNTS).exchange().expectStatus()
				.isUnauthorized();
		addArtifact().isUnauthorized();
		closeBug().isUnauthorized();
		addProgrammer().isUnauthorized();
		openBug().isUnauthorized();
		openAndAssignBug().isUnauthorized();
		assignBug().isUnauthorized();
	}

	@Test
	@WithMockUser(username = "authorizeduser")
	void authorizedUser() {
		testClient.get().uri(BUGS + EMAIL_BUGS_COUNTS).exchange().expectStatus().isOk();
		testClient.get().uri(INFO_BACK_OFFICE + LOGS_CONTROLLER + EMAIL_BUGS_COUNTS).exchange().expectStatus()
				.isForbidden();
		testClient.get().uri(INFO_BACK_OFFICE + STATISTICS_CONTROLLER + EMAIL_BUGS_COUNTS).exchange().expectStatus()
				.isForbidden();
		addArtifact().isForbidden();
		closeBug().isForbidden();
		addProgrammer().isForbidden();
		openBug().isForbidden();
		openAndAssignBug().isForbidden();
		assignBug().isForbidden();
	}

	private static String getAuthorizationTocken(String username, String password) {
		String rowText = username + ":" + password;
		return "Basic " + Base64.getEncoder().encodeToString(rowText.getBytes());
	}

	public void commonGetRequests() {
		testClient.get().uri(BUGS + EMAIL_BUGS_COUNTS).exchange().expectStatus().isOk();
		testClient.get().uri(INFO_BACK_OFFICE + LOGS_CONTROLLER + EMAIL_BUGS_COUNTS).exchange().expectStatus()
				.isForbidden();
		testClient.get().uri(INFO_BACK_OFFICE + STATISTICS_CONTROLLER + EMAIL_BUGS_COUNTS).exchange().expectStatus()
				.isForbidden();
	}

	public void retriveInformationAboutLogs() {
		testClient.get().uri(BUGS + EMAIL_BUGS_COUNTS).exchange().expectStatus().isOk();
		testClient.get().uri(INFO_BACK_OFFICE + LOGS_CONTROLLER + EMAIL_BUGS_COUNTS).exchange().expectStatus().isOk();
		testClient.get().uri(INFO_BACK_OFFICE + STATISTICS_CONTROLLER + EMAIL_BUGS_COUNTS).exchange().expectStatus()
				.isOk();
	}

	public StatusAssertions assignBug() {
		return testClient.put().uri(BUGS + ASSIGN).exchange().expectStatus();
	}

	public StatusAssertions openAndAssignBug() {
		return testClient.post().uri(BUGS + OPEN + ASSIGN).exchange().expectStatus();
	}

	public StatusAssertions openBug() {
		return testClient.post().uri(BUGS + OPEN).exchange().expectStatus();
	}

	public StatusAssertions addProgrammer() {
		return testClient.post().uri(BUGS + PROGRAMMERS).exchange().expectStatus();
	}

	public StatusAssertions closeBug() {
		return testClient.put().uri(BUGS + CLOSE).exchange().expectStatus();
	}

	public StatusAssertions addArtifact() {
		return testClient.post().uri(BUGS + ARTIFACTS).exchange().expectStatus();
	}
}
