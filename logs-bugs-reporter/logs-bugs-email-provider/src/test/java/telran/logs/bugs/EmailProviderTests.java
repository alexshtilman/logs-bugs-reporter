package telran.logs.bugs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureDataJpa
@AutoConfigureWebTestClient
class EmailProviderTests {
	@Autowired
	WebTestClient webClient;

	@Test
	@Sql("fillTabels.sql")
	void testEmailExist() {
		webClient.get()
		.uri("/email/LogTypeAndCountDto.class")
		.exchange()
		.expectStatus().isOk()
		.expectBody(String.class)
		.isEqualTo("Avior@gmail.com");
	}
}
