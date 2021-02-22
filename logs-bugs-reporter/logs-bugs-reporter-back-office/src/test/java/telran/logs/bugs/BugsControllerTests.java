package telran.logs.bugs;

import static telran.logs.bugs.api.BugsApi.ASSIGN;
import static telran.logs.bugs.api.BugsApi.BUGS;
import static telran.logs.bugs.api.BugsApi.OPEN;
import static telran.logs.bugs.api.BugsApi.PROGRAMMERS;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.ProgrammerDto;

/**
 * 
 */

/**
 * @author Alex Shtilman Feb 22, 2021
 *
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@AutoConfigureDataJpa
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BugsControllerTests {

	private static final String SQL_FILE = "fill_db.sql";

	WebTestClient webClient;

	@BeforeAll
	void initClient() {
		webClient = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build();
	}

	@Test
	@Sql(SQL_FILE)
	void testGetProgrammersBugsById() {
		List<BugResponseDto> expected = new ArrayList<>();
		expected.add(new BugResponseDto(null, null, null, 0, null, null, null, 0));
		expected.add(new BugResponseDto(null, null, null, 0, null, null, null, 0));
		expected.add(new BugResponseDto(null, null, null, 0, null, null, null, 0));
		webClient.get().uri(BUGS + PROGRAMMERS + "/1").exchange().expectStatus().isOk()
				.expectBodyList(BugResponseDto.class).isEqualTo(expected);
	}

	@Test
	@Sql(SQL_FILE)
	void testPostAddProgrammer() {
		ProgrammerDto programmerDto = new ProgrammerDto();
		webClient.post().uri(BUGS + PROGRAMMERS).contentType(MediaType.APPLICATION_JSON).bodyValue(programmerDto)
				.exchange().expectStatus().isOk().expectBody(ProgrammerDto.class).isEqualTo(programmerDto);
	}

	@Test
	@Sql(SQL_FILE)
	void testPostOpenBug() {
		BugDto bugDto = new BugDto();
		webClient.post().uri(BUGS + OPEN).contentType(MediaType.APPLICATION_JSON).bodyValue(bugDto).exchange()
				.expectStatus().isOk().expectBody(BugDto.class).isEqualTo(bugDto);
	}

	@Test
	@Sql(SQL_FILE)
	void testPostOpenAndAssignBug() {
		BugResponseDto bugResponseDto = new BugResponseDto(null, null, null, 0, null, null, null, 0);
		webClient.post().uri(BUGS + OPEN + ASSIGN).contentType(MediaType.APPLICATION_JSON).bodyValue(bugResponseDto)
				.exchange().expectStatus().isOk().expectBody(BugResponseDto.class).isEqualTo(bugResponseDto);
	}

	@Test
	@Sql(SQL_FILE)
	void testPutAssignBug() {
		BugResponseDto bugResponseDto = new BugResponseDto(null, null, null, 0, null, null, null, 0);
		webClient.put().uri(BUGS + ASSIGN).contentType(MediaType.APPLICATION_JSON).bodyValue(bugResponseDto).exchange()
				.expectStatus().isOk().expectBody(BugResponseDto.class).isEqualTo(bugResponseDto);
	}
}
