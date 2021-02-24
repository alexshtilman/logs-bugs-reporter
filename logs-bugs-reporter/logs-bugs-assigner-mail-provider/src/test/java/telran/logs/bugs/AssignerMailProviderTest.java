package telran.logs.bugs;

import static telran.logs.bugs.api.Constants.GET_ASIGNER_EMAIL;
import static telran.logs.bugs.api.Constants.MAIL_CONTROLLER;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * 
 * @author Alex Shtilman Feb 22, 2021
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AssignerMailProviderTest {
	@Autowired
	WebTestClient webClient;

	@Test
	void testEmailExist() {
		webClient.get().uri(MAIL_CONTROLLER + GET_ASIGNER_EMAIL).exchange().expectStatus().isOk()
				.expectBody(String.class).isEqualTo("test@gmail.com");
	}
}
