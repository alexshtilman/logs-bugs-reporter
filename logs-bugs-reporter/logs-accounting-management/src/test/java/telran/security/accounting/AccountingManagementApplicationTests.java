package telran.security.accounting;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static telran.security.accounting.api.Constants.ACCOUNTS;
import static telran.security.accounting.api.Constants.ACTIVATED;
import static telran.security.accounting.api.Constants.ADD;
import static telran.security.accounting.api.Constants.ASSIGN;
import static telran.security.accounting.api.Constants.CLEAR;
import static telran.security.accounting.api.Constants.PASSWORD;
import static telran.security.accounting.api.Constants.ROLE;
import static telran.security.accounting.api.Constants.UPDATE;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import telran.security.accounting.dto.AccountPassword;
import telran.security.accounting.dto.AccountRequest;
import telran.security.accounting.dto.AccountResponse;
import telran.security.accounting.dto.AccountRole;
import telran.security.accounting.mongo.documents.AccountDocument;
import telran.security.accounting.repo.AccountRepository;

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class AccountingManagementApplicationTests {
	private static final String USER_MOSHE = "Moshe";
	private static final String ASTERICS8 = "*".repeat(8);
	private static final String PASSWORD_MOSHE = "12345.com";
	private static final String[] ROLES_MOSHE = { "USER", "ADMIN" };
	private static final long EXPIRATION_MOSHE = 1;
	private static final String EXPIRED = "expired1";

	@Autowired
	WebTestClient testClient;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	AccountRepository accountRepository;
	AccountRequest accountRequestMoshe = new AccountRequest(USER_MOSHE, PASSWORD_MOSHE, ROLES_MOSHE, EXPIRATION_MOSHE);
	AccountResponse expectedMoshe = new AccountResponse(USER_MOSHE, "{noop}" + PASSWORD_MOSHE, ROLES_MOSHE,
			System.currentTimeMillis() / 1000 + EXPIRATION_MOSHE * 60);
	AccountResponse expectedResponseRoleRemove;
	private long currentTimestamp = System.currentTimeMillis() / 1000;
	private long futureTimestamp = currentTimestamp + 1000000;
	private long pastTimestamp = currentTimestamp - 1000000;
	AccountDocument activated1 = new AccountDocument("activated1", "password", new String[0], currentTimestamp,
			futureTimestamp);
	AccountDocument activated2 = new AccountDocument("activated2", "password", new String[0], currentTimestamp,
			futureTimestamp);
	AccountDocument expired1 = new AccountDocument(EXPIRED, "password", new String[0], currentTimestamp, pastTimestamp);

	@Test

	@Order(1)
	void addAccount() {
		testClient.post().uri(ACCOUNTS + ADD).bodyValue(accountRequestMoshe).exchange().expectStatus().isOk()
				.expectBody(AccountResponse.class).isEqualTo(expectedMoshe);

	}

	@Test
	@Order(2)
	void getAccount() {
		AccountResponse response = getMosheAccount();
		assertEquals(expectedMoshe, response);
		assertTrue(passwordEncoder.matches(PASSWORD_MOSHE, response.password));

	}

	private AccountResponse getMosheAccount() {
		return testClient.get().uri(ACCOUNTS + "/Moshe").exchange().expectStatus().isOk()
				.returnResult(AccountResponse.class).getResponseBody().blockFirst();
	}

	@Test
	@Order(4)
	void updatePassword() {
		AccountPassword accountPassword = getAccountPassword(PASSWORD_MOSHE + "new");
		AccountResponse response = testClient.put().uri(ACCOUNTS + UPDATE + PASSWORD).bodyValue(accountPassword)
				.exchange().expectStatus().isOk().returnResult(AccountResponse.class).getResponseBody().blockFirst();
		AccountResponse responseUpdated = getMosheAccount();
		assertEquals(ASTERICS8, response.password);
		assertTrue(passwordEncoder.matches(PASSWORD_MOSHE + "new", responseUpdated.password));

	}

	private AccountPassword getAccountPassword(String password) {
		AccountPassword accountPassword = new AccountPassword(USER_MOSHE, password);

		return accountPassword;
	}

	@Test
	@Order(3)
	void updateSamePassword() {
		// FIXME should be updated after exception handling to isBadRequest()

		AccountPassword accountPassword = getAccountPassword(PASSWORD_MOSHE);
		testClient.put().uri(ACCOUNTS + UPDATE + PASSWORD).bodyValue(accountPassword).exchange().expectStatus()
				.is5xxServerError();
	}

	@Test
	@Order(5)
	void addRole() {
		AccountRole accountRole = getAccountRole("STAT");
		expectedResponseRoleRemove = testClient.put().uri(ACCOUNTS + ROLE + ASSIGN).bodyValue(accountRole).exchange()
				.expectStatus().isOk().returnResult(AccountResponse.class).getResponseBody().blockFirst();
		assertEquals(expectedResponseRoleRemove, expectedMoshe);
		assertEquals(ASTERICS8, expectedResponseRoleRemove.password);
		AccountResponse response = getMosheAccount();
		String expectedRoles[] = { "USER", "ADMIN", "STAT" };
		assertArrayEquals(expectedRoles, response.roles);
	}

	@Test
	@Order(6)
	void removeRole() {
		AccountRole accountRole = getAccountRole("STAT");
		AccountResponse response = testClient.put().uri(ACCOUNTS + ROLE + CLEAR).bodyValue(accountRole).exchange()
				.expectStatus().isOk().returnResult(AccountResponse.class).getResponseBody().blockFirst();

		assertEquals(ASTERICS8, response.password);
		response = getMosheAccount();

		assertEquals(expectedMoshe, response);
	}

	@Test
	@Order(7)
	void removeAccount() {
		testClient.delete().uri(ACCOUNTS + "/" + USER_MOSHE).exchange().expectStatus().isOk();
		assertNull(accountRepository.findById(USER_MOSHE).orElse(null));
	}

	@Test
	@Order(8)
	void getActivatedAccounts() {
		accountRepository.saveAll(Arrays.asList(activated1, activated2, expired1));
		testClient.get().uri(ACCOUNTS + ACTIVATED).exchange().expectStatus().isOk()
				.expectBodyList(AccountResponse.class).isEqualTo(getExpectedAccounts());
	}

	@Test
	@Order(9)
	void getExpiredAccount() {
		testClient.get().uri(ACCOUNTS + "/" + EXPIRED).exchange().expectStatus().isOk()
				.expectBody(AccountResponse.class).isEqualTo(null);
	}

	private List<AccountResponse> getExpectedAccounts() {

		return Arrays.asList(
				new AccountResponse(activated1.getUsername(), activated1.getPassword(), activated1.getRoles(),
						activated1.getActivationTimestamp()),
				new AccountResponse(activated2.getUsername(), activated2.getPassword(), activated2.getRoles(),
						activated2.getActivationTimestamp()));
	}

	private AccountRole getAccountRole(String role) {
		AccountRole res = new AccountRole(USER_MOSHE, role);

		return res;
	}

}
