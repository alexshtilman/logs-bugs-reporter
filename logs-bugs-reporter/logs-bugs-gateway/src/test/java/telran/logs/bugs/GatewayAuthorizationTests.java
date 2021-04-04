package telran.logs.bugs;

import static telran.logs.bugs.api.Constants.ARTIFACTS;
import static telran.logs.bugs.api.Constants.BUGS_CONTROLLER;
import static telran.logs.bugs.api.Constants.CLOSE;
import static telran.logs.bugs.api.Constants.LOGS_CONTROLLER;
import static telran.logs.bugs.api.Constants.OPEN;
import static telran.logs.bugs.api.Constants.PROGRAMMERS;
import static telran.logs.bugs.api.Constants.STATISTICS_CONTROLLER;
import static telran.logs.bugs.configuration.Constants.INFO_BACK_OFFICE;
import static telran.logs.bugs.configuration.Constants.REPORTER_BACK_OFFICE;
import static telran.security.accounting.api.Constants.ASSIGN;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Alex Shtilman Apr 2, 2021
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "spring.cloud.config.enabled=false" })
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class GatewayAuthorizationTests {

	private static final String BUGS = REPORTER_BACK_OFFICE + BUGS_CONTROLLER;
	private static final String ANY_QUERY = "/any_query?any_param=123";

	@Autowired
	WebTestClient testClient;

	// INFO_BACK_OFFICE + LOGS_CONTROLLER & STATISTICS_CONTROLLER only get
	@Test
	@WithMockUser(roles = { "DEVELOPER" })
	void perforDevelpersQuerys() {
		retriveInformationAboutLogs();
		openBug().isNotFound();
		openAndAssignBug().isNotFound();

		closeBug().isForbidden();
		addArtifact().isForbidden();
		addProgrammer().isForbidden();
		assignBug().isForbidden();
	}

	@Test
	@WithMockUser(roles = { "TESTER" })
	void perforTestersQuerys() {
		commonGetRequests();
		openBug().isNotFound();
		openAndAssignBug().isNotFound();
		closeBug().isNotFound();

		addArtifact().isForbidden();
		addProgrammer().isForbidden();
	}

	@Test
	@WithMockUser(roles = { "ASSIGNER" })
	void perforAssgnerQuerys() {
		commonGetRequests();
		openBug().isNotFound();
		openAndAssignBug().isNotFound();
		assignBug().isNotFound();
		addArtifact().isNotFound();

		closeBug().isForbidden();
		addProgrammer().isForbidden();
	}

	@Test
	@WithMockUser(roles = { "PROJECT_OWNER" })
	void perforProjectOwnerQuerys() {
		commonGetRequests();
		addProgrammer().isNotFound();

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
		addArtifact().isNotFound();

		closeBug().isForbidden();
		addProgrammer().isForbidden();

		openBug().isForbidden();
		openAndAssignBug().isForbidden();
		assignBug().isForbidden();
	}

	@Test
	void unauthUser() {
		testClient.get().uri(BUGS + ANY_QUERY).exchange().expectStatus().isUnauthorized();
		testClient.get().uri(INFO_BACK_OFFICE + LOGS_CONTROLLER + ANY_QUERY).exchange().expectStatus().isUnauthorized();
		testClient.get().uri(INFO_BACK_OFFICE + STATISTICS_CONTROLLER + ANY_QUERY).exchange().expectStatus()
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
		testClient.get().uri(BUGS + ANY_QUERY).exchange().expectStatus().isNotFound();
		testClient.get().uri(INFO_BACK_OFFICE + LOGS_CONTROLLER + ANY_QUERY).exchange().expectStatus().isForbidden();
		testClient.get().uri(INFO_BACK_OFFICE + STATISTICS_CONTROLLER + ANY_QUERY).exchange().expectStatus()
				.isForbidden();
		addArtifact().isForbidden();
		closeBug().isForbidden();
		addProgrammer().isForbidden();
		openBug().isForbidden();
		openAndAssignBug().isForbidden();
		assignBug().isForbidden();
	}

	public void commonGetRequests() {
		testClient.get().uri(BUGS + ANY_QUERY).exchange().expectStatus().isNotFound();
		testClient.get().uri(INFO_BACK_OFFICE + LOGS_CONTROLLER + ANY_QUERY).exchange().expectStatus().isForbidden();
		testClient.get().uri(INFO_BACK_OFFICE + STATISTICS_CONTROLLER + ANY_QUERY).exchange().expectStatus()
				.isForbidden();
	}

	public void retriveInformationAboutLogs() {
		testClient.get().uri(BUGS + ANY_QUERY).exchange().expectStatus().isNotFound();
		testClient.get().uri(INFO_BACK_OFFICE + LOGS_CONTROLLER + ANY_QUERY).exchange().expectStatus().isNotFound();
		testClient.get().uri(INFO_BACK_OFFICE + STATISTICS_CONTROLLER + ANY_QUERY).exchange().expectStatus()
				.isNotFound();
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
