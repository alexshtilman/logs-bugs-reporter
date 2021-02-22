package telran.logs.bugs;

import static telran.logs.bugs.api.Constants.MAIL_CONTROLLER;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * 
 * @author Alex Shtilman Feb 22, 2021
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureDataJpa
@AutoConfigureWebTestClient

class EmailProviderTests {

	@Autowired
	WebTestClient webClient;

	@Test
	@Sql("fillTabels.sql")
	void testEmailExist() {
		webClient.get().uri(MAIL_CONTROLLER + "/LogTypeAndCountDto.class").exchange().expectStatus().isOk()
				.expectBody(String.class).isEqualTo("Avior@gmail.com");
	}

	@Test
	@Sql("fillTabels.sql")
	void emailNoExisting() {
		webClient.get().uri(MAIL_CONTROLLER + "/artfact1").exchange().expectStatus().isOk().expectBody(String.class)
				.isEqualTo(null);
	}
}
