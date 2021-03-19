package telran.security.accounting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static telran.security.accounting.api.ApiConstants.ACCOUNT;
import static telran.security.accounting.api.ApiConstants.ADD;
import static telran.security.accounting.api.ApiConstants.ASSING;
import static telran.security.accounting.api.ApiConstants.CLEAR;
import static telran.security.accounting.api.ApiConstants.PASSWORD;
import static telran.security.accounting.api.ApiConstants.ROLE;
import static telran.security.accounting.api.ApiConstants.UPDATE;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import telran.security.accounting.dto.AccountRequest;
import telran.security.accounting.dto.AccountResponse;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@EnableAutoConfiguration
@AutoConfigureDataMongo
public class AccountingmanagementTests {

	/**
	 * 
	 */
	private static final String MOSHE = "/moshe";

	@Autowired
	WebTestClient webClient;

	String[] roles = { "POST", "GET" };

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Order(1)
	void postAccountDoc() {

		AccountRequest request = new AccountRequest("moshe", "12345678.com", roles, 999);
		AccountResponse data = webClient.post().uri(ACCOUNT + ADD)
				.contentType(MediaType.APPLICATION_JSON).bodyValue(request).exchange()
				.expectStatus().isOk()
				.expectBody(AccountResponse.class).returnResult().getResponseBody();

		AccountResponse expected = webClient.get().uri(ACCOUNT + MOSHE).exchange().expectBody(AccountResponse.class)
				.returnResult().getResponseBody();
		assertEquals(expected, data);

		webClient.post().uri(ACCOUNT + ADD).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(new AccountRequest(null, "12345678.com", roles, 999)).exchange().expectStatus()
				.isBadRequest();
		webClient.post().uri(ACCOUNT + ADD).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(new AccountRequest("sara", "1234567", roles, 999)).exchange().expectStatus().isBadRequest();

		webClient.post().uri(ACCOUNT + ADD).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(new AccountRequest("sara", "12345678.com", roles, -1)).exchange().expectStatus()
				.isBadRequest();
	}


	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Order(2)
	void putUpdatePassword() {
		AccountResponse data = webClient.put().uri(ACCOUNT + UPDATE + PASSWORD + "?username=moshe&password=newpassword")
				.exchange().expectStatus().isOk()
				.expectBody(AccountResponse.class).returnResult().getResponseBody();
		AccountResponse expected = webClient.get().uri(ACCOUNT + MOSHE).exchange().expectBody(AccountResponse.class)
				.returnResult().getResponseBody();
		assertEquals(expected, data);

		webClient.put().uri(ACCOUNT + UPDATE + PASSWORD + "?username=sara&password=newpassword").exchange()
				.expectStatus().isNotFound();

		webClient.put().uri(ACCOUNT + UPDATE + PASSWORD + "?username=moshe&password=").exchange().expectStatus()
				.isBadRequest();

		webClient.put().uri(ACCOUNT + UPDATE + PASSWORD + "?username=moshe&password=123").exchange().expectStatus()
				.isBadRequest();

		webClient.put().uri(ACCOUNT + UPDATE + PASSWORD + "?username=&password=xxx").exchange().expectStatus()
				.isBadRequest();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Order(3)
	void putAssignRole() {
		AccountResponse data = webClient.put().uri(ACCOUNT + ROLE + ASSING + "?username=moshe&role=HEADER")
				.exchange().expectStatus().isOk().expectBody(AccountResponse.class).returnResult().getResponseBody();
		AccountResponse expected = webClient.get().uri(ACCOUNT + MOSHE).exchange().expectBody(AccountResponse.class)
				.returnResult().getResponseBody();
		assertEquals(expected, data);

		webClient.put().uri(ACCOUNT + ROLE + ASSING + "?username=sara&role=HEADER").exchange().expectStatus()
				.isNotFound();

		webClient.put().uri(ACCOUNT + ROLE + ASSING + "?username=moshe&role=").exchange().expectStatus().isBadRequest();

		webClient.put().uri(ACCOUNT + ROLE + ASSING + "?username=&role=HEADER").exchange().expectStatus()
				.isBadRequest();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Order(4)
	void putClearRole() {
		AccountResponse data = webClient.put().uri(ACCOUNT + ROLE + CLEAR + "?username=moshe&role=GET").exchange()
				.expectStatus().isOk()
				.expectBody(AccountResponse.class).returnResult().getResponseBody();
		AccountResponse expected = webClient.get().uri(ACCOUNT + MOSHE).exchange().expectBody(AccountResponse.class)
				.returnResult().getResponseBody();
		assertEquals(expected, data);

		webClient.put().uri(ACCOUNT + ROLE + CLEAR + "?username=sara&role=HEADER").exchange().expectStatus()
				.isNotFound();

		webClient.put().uri(ACCOUNT + ROLE + CLEAR + "?username=moshe&role=").exchange().expectStatus().isBadRequest();

		webClient.put().uri(ACCOUNT + ROLE + CLEAR + "?username=&role=HEADER").exchange().expectStatus()
				.isBadRequest();
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	@Order(5)
	void notAlowed() {
		webClient.get().uri(ACCOUNT + MOSHE).exchange().expectStatus().isOk();

		AccountRequest request = new AccountRequest("sara", "12345678.com", roles, 999);
		webClient.post().uri(ACCOUNT + ADD).contentType(MediaType.APPLICATION_JSON).bodyValue(request).exchange()
				.expectStatus().isForbidden();
		webClient.put().uri(ACCOUNT + UPDATE + PASSWORD + "?username=moshe&password=newpassword").exchange()
				.expectStatus().isForbidden();
		webClient.put().uri(ACCOUNT + ROLE + ASSING + "?username=moshe&role=HEADER").exchange().expectStatus()
				.isForbidden();
		webClient.put().uri(ACCOUNT + ROLE + CLEAR + "?username=moshe&role=GET").exchange().expectStatus()
				.isForbidden();
		webClient.delete().uri(ACCOUNT + MOSHE).exchange().expectStatus().isForbidden();

	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Order(6)
	void deleteAccountDoc() {
		webClient.delete().uri(ACCOUNT + MOSHE).exchange().expectStatus().isOk();
		webClient.get().uri(ACCOUNT + MOSHE).exchange().expectStatus().isNotFound();
	}

}
