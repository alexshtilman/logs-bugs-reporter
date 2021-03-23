package telran.security.accounting;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static telran.security.accounting.api.ApiConstants.ACCOUNT;
import static telran.security.accounting.api.ApiConstants.ADD;
import static telran.security.accounting.api.ApiConstants.ASSING;
import static telran.security.accounting.api.ApiConstants.CLEAR;
import static telran.security.accounting.api.ApiConstants.PASSWORD;
import static telran.security.accounting.api.ApiConstants.ROLE;
import static telran.security.accounting.api.ApiConstants.UPDATE;

import org.junit.jupiter.api.BeforeEach;
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
	private static final String PROTECTED_PASSWORD = "********";

	@Autowired
	WebTestClient webClient;

	String[] roles = new String[] {};
	AccountRequest moshe;

	@BeforeEach
	void reset() {
		moshe = new AccountRequest("moshe", "12345678.com", roles, 999);
		roles = new String[] { "POST", "GET" };
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Order(1)
	void postAccountDoc() {
		AccountResponse data = webClient.post().uri(ACCOUNT + ADD)
				.contentType(MediaType.APPLICATION_JSON).bodyValue(moshe).exchange()
				.expectStatus().isOk()
				.expectBody(AccountResponse.class).returnResult().getResponseBody();

		assertEquals(data.username, moshe.username);
		assertEquals(data.password, "{noop}" + moshe.password);
		assertArrayEquals(data.roles, moshe.roles);

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
	void getAccountDoc() {
		AccountResponse expected = webClient.get().uri(ACCOUNT + MOSHE).exchange().expectBody(AccountResponse.class)
				.returnResult().getResponseBody();
		assertEquals(expected.username, moshe.username);
		assertEquals(expected.password, "{noop}" + moshe.password);
		assertArrayEquals(expected.roles, moshe.roles);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Order(3)
	void putUpdatePassword() {
		AccountResponse data = webClient.put().uri(ACCOUNT + UPDATE + PASSWORD + "?username=moshe")
				.contentType(MediaType.APPLICATION_JSON).bodyValue("12345678.com777")
				.exchange().expectStatus().isOk()
				.expectBody(AccountResponse.class).returnResult().getResponseBody();

		AccountResponse expected = webClient.get().uri(ACCOUNT + MOSHE).exchange().expectBody(AccountResponse.class)
				.returnResult().getResponseBody();
		assertEquals(expected, data);

		webClient.put().uri(ACCOUNT + UPDATE + PASSWORD + "?username=sara").contentType(MediaType.APPLICATION_JSON)
				.bodyValue("newpassword").exchange()
				.expectStatus().isNotFound();

		webClient.put().uri(ACCOUNT + UPDATE + PASSWORD + "?username=moshe").contentType(MediaType.APPLICATION_JSON)
				.bodyValue("").exchange().expectStatus()
				.isBadRequest();

		webClient.put().uri(ACCOUNT + UPDATE + PASSWORD + "?username=moshe").contentType(MediaType.APPLICATION_JSON)
				.bodyValue("123").exchange().expectStatus()
				.isBadRequest();

		webClient.put().uri(ACCOUNT + UPDATE + PASSWORD + "?username=").contentType(MediaType.APPLICATION_JSON)
				.bodyValue("12345678.com").exchange().expectStatus()
				.isBadRequest();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Order(4)
	void putAssignRole() {
		AccountResponse data = webClient.put().uri(ACCOUNT + ROLE + ASSING + "?username=moshe&role=HEADER")
				.exchange().expectStatus().isOk().expectBody(AccountResponse.class).returnResult().getResponseBody();
		roles = new String[] { "POST", "GET", "HEADER" };
		assertEquals(moshe.username, data.username);
		assertEquals(PROTECTED_PASSWORD, data.password);
		assertArrayEquals(roles, data.roles);

		webClient.put().uri(ACCOUNT + ROLE + ASSING + "?username=sara&role=HEADER").exchange().expectStatus()
				.isNotFound();

		webClient.put().uri(ACCOUNT + ROLE + ASSING + "?username=moshe&role=").exchange().expectStatus().isBadRequest();

		webClient.put().uri(ACCOUNT + ROLE + ASSING + "?username=&role=HEADER").exchange().expectStatus()
				.isBadRequest();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	@Order(5)
	void putClearRole() {
		AccountResponse data = webClient.put().uri(ACCOUNT + ROLE + CLEAR + "?username=moshe&role=GET").exchange()
				.expectStatus().isOk()
				.expectBody(AccountResponse.class).returnResult().getResponseBody();
		roles = new String[] { "POST", "HEADER" };
		assertEquals(moshe.username, data.username);
		assertEquals(PROTECTED_PASSWORD, data.password);
		assertArrayEquals(roles, data.roles);

		webClient.put().uri(ACCOUNT + ROLE + CLEAR + "?username=sara&role=HEADER").exchange().expectStatus()
				.isNotFound();

		webClient.put().uri(ACCOUNT + ROLE + CLEAR + "?username=moshe&role=").exchange().expectStatus().isBadRequest();

		webClient.put().uri(ACCOUNT + ROLE + CLEAR + "?username=&role=HEADER").exchange().expectStatus()
				.isBadRequest();
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	@Order(6)
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
	@Order(7)
	void deleteAccountDoc() {
		webClient.delete().uri(ACCOUNT + MOSHE).exchange().expectStatus().isOk();
		webClient.get().uri(ACCOUNT + MOSHE).exchange().expectStatus().isOk().equals(null);
	}

}
