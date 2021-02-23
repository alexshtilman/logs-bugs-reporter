package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static telran.logs.bugs.api.Constants.ASSIGN;
import static telran.logs.bugs.api.Constants.BUGS_CONTROLLER;
import static telran.logs.bugs.api.Constants.OPEN;
import static telran.logs.bugs.api.Constants.PROGRAMMERS;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.OpenningMethod;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.repo.BugRepo;
import telran.logs.bugs.jpa.repo.ProgrammerRepo;

/**
 * 
 */

/**
 * @author Alex Shtilman Feb 22, 2021
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureDataJpa
class BugsControllerTests {

	private static final String SQL_FILE = "fill_db.sql";

	WebTestClient webClient;

	BugRepo bugsRepo;
	ProgrammerRepo programmerRepo;

	@Autowired
	public BugsControllerTests(BugRepo bugsRepo, ProgrammerRepo programmerRepo, WebTestClient webClient) {
		this.bugsRepo = bugsRepo;
		this.programmerRepo = programmerRepo;
		this.webClient = webClient;
	}

	@Test
	@Sql(SQL_FILE)
	void testGetProgrammersBugsById() {
		List<BugResponseDto> expected = new ArrayList<>();

		expected.add(new BugResponseDto(Seriousness.BLOCKING, "BLOCKING bug description", LocalDate.of(1991, 1, 1), 1,
				null, BugStatus.ASSIGNED, OpenningMethod.MANUAL, 1));

		expected.add(new BugResponseDto(Seriousness.CRITICAL, "CRITICAL bug description", LocalDate.of(1991, 1, 1), 1,
				null, BugStatus.OPEND, OpenningMethod.AUTOMATIC, 2));

		expected.add(new BugResponseDto(Seriousness.MINOR, "MINOR bug description", LocalDate.of(1991, 1, 1), 1,
				LocalDate.of(2018, 1, 1), BugStatus.CLOSED, OpenningMethod.AUTOMATIC, 3));

		webClient.get().uri(BUGS_CONTROLLER + PROGRAMMERS + "/1").exchange().expectStatus().isOk()
				.expectBodyList(BugResponseDto.class).isEqualTo(expected);

		webClient.get().uri(BUGS_CONTROLLER + PROGRAMMERS + "/Vasya").exchange().expectStatus().isBadRequest();

		webClient.get().uri(BUGS_CONTROLLER + PROGRAMMERS + "/999").exchange().expectStatus().isOk()
				.expectBodyList(BugResponseDto.class).isEqualTo(Collections.emptyList());
	}

	@Test
	@Sql(SQL_FILE)
	void testPostAddProgrammer() {
		ProgrammerDto expected = new ProgrammerDto(6, "Alex", "alex@gmail.com");
		ProgrammerDto invalid = new ProgrammerDto(-1, null, "ex@ist@x.0.1");

		testAssertions(BUGS_CONTROLLER + PROGRAMMERS, ProgrammerDto.class, expected, invalid);
		Programmer programmer = programmerRepo.findById(expected.id).orElse(null);

		assertEquals(expected.email, programmer.getEmail());
		assertEquals(expected.name, programmer.getName());
	}

	@Test
	@Sql(SQL_FILE)
	void testPostOpenBug() {

		BugDto dto = new BugDto(Seriousness.BLOCKING, "Description", LocalDate.now());
		BugResponseDto expected = new BugResponseDto(dto.seriousness, dto.description, dto.dateOpen, 0, null,
				BugStatus.OPEND, OpenningMethod.MANUAL, 6);

		BugDto invalid = new BugDto(null, "", null);
		testAssertions(BUGS_CONTROLLER + OPEN, BugResponseDto.class, dto, expected, invalid);

		findBugByIdAndAssert(expected);
	}

	@Test
	@Sql(SQL_FILE)
	void testPostOpenAndAssignBug() {
		BugAssignDto dto = BugAssignDto.builder().dateOpen(LocalDate.now()).description("Description")
				.seriousness(Seriousness.BLOCKING).programmerId(1).build();

		BugResponseDto expected = BugResponseDto.builder().bugId(6).dateClose(null).dateOpen(dto.dateOpen)
				.description(dto.description).seriousness(dto.seriousness).status(BugStatus.ASSIGNED)
				.openningMethod(OpenningMethod.MANUAL).programmerId(1).build();

		BugAssignDto invalid = new BugAssignDto(null, null, null, 0);
		testAssertions(BUGS_CONTROLLER + OPEN + ASSIGN, BugResponseDto.class, dto, expected, invalid);

		findBugByIdAndAssert(expected);

	}

	@Test
	@Sql(SQL_FILE)
	void testPutAndAssignBug() {
		AssignBugData dto = new AssignBugData(4, 1, "assigned!");

		webClient.put().uri(BUGS_CONTROLLER + ASSIGN).contentType(MediaType.APPLICATION_JSON).bodyValue(dto).exchange()
				.expectStatus().isOk();

		Bug bug = bugsRepo.findById(dto.bugId).orElse(null);
		assertEquals("CRITICAL bug description%n Assigment Description " + dto.description, bug.getDescription());
		assertEquals(dto.programmerId, bug.getProgrammer().getId());

	}

	public <T> ResponseSpec getResponceFromPost(String uri, T bodyValue) {
		return webClient.post().uri(uri).contentType(MediaType.APPLICATION_JSON).bodyValue(bodyValue).exchange();
	}

	public <T, P> void expectOkAndEqual(String uri, T bodyValue, P expected, Class<P> clazz) {
		getResponceFromPost(uri, bodyValue).expectStatus().isOk().expectBody(clazz).isEqualTo(expected);
	}

	public <T> void expectBadRequest(String uri, T bodyValue) {
		getResponceFromPost(uri, bodyValue).expectStatus().isBadRequest();
	}

	public <T> void testAssertions(String uri, Class<T> clazz, T expected, T invalid) {
		testAssertions(uri, clazz, expected, expected, invalid);
	}

	public <T, P> void testAssertions(String uri, Class<P> clazz, T bodyValue, P expected, T invalid) {
		expectOkAndEqual(uri, bodyValue, expected, clazz);
		expectBadRequest(uri, invalid);
	}

	public void findBugByIdAndAssert(BugResponseDto expected) {
		Bug bug = bugsRepo.findById(expected.bugId).orElse(null);
		BugResponseDto expectedRepo = new BugResponseDto(bug.getSeriosness(), bug.getDescription(), bug.getDateOppen(),
				0, bug.getDateClose(), bug.getStatus(), bug.getOppeningMethod(), bug.getId());
		assertEquals(expectedRepo, expected);
	}

}
