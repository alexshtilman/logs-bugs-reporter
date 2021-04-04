package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static telran.security.accounting.api.Constants.ACCOUNTS_CONTROLLER;
import static telran.security.accounting.api.Constants.ACTIVATED;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import telran.logs.bugs.repo.AccountRepository;
import telran.security.accounting.dto.AccountResponse;
import telran.security.accounting.mongo.documents.AccountDocument;

/**
 * 
 */

/**
 * @author Alex Shtilman Apr 2, 2021
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@EnableAutoConfiguration
@AutoConfigureDataMongo
public class AccountProviderTests {

	@Autowired
	WebTestClient client;

	@Autowired
	AccountRepository repository;

	@Test
	@Order(1)
	void filldb() {
		long currentTimestamp = System.currentTimeMillis() / 1000;
		long expirationTimestamp = currentTimestamp + 1000000;
		long pastTimestamp = currentTimestamp - 1000000;
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

		AccountDocument admin = new AccountDocument("admin", encoder.encode("admin123"), new String[] { "ADMIN" },
				currentTimestamp, expirationTimestamp);
		AccountDocument expired = new AccountDocument("emposter", encoder.encode("exipred123"), new String[] { "USER" },
				currentTimestamp, pastTimestamp);
		AccountDocument user = new AccountDocument("user", encoder.encode("user123"), new String[] { "USER" },
				currentTimestamp, expirationTimestamp);
		repository.saveAll(Arrays.asList(admin, expired, user));
	}

	@Test
	@Order(2)
	void testGet() {
		List<AccountResponse> responce = client.get().uri(ACCOUNTS_CONTROLLER + ACTIVATED).exchange().expectStatus()
				.isOk().returnResult(AccountResponse.class).getResponseBody().collectList().block();
		assertEquals(2, responce.size());
	}
}
