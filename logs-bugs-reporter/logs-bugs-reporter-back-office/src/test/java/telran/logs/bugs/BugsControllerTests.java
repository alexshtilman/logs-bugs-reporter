package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static telran.logs.bugs.api.Constants.ARTIFACTS;
import static telran.logs.bugs.api.Constants.ASSIGN;
import static telran.logs.bugs.api.Constants.BUGS_CONTROLLER;
import static telran.logs.bugs.api.Constants.CLOSE;
import static telran.logs.bugs.api.Constants.EMAIL_BUGS_COUNTS;
import static telran.logs.bugs.api.Constants.LEAST_BUGS;
import static telran.logs.bugs.api.Constants.MOST_BUGS;
import static telran.logs.bugs.api.Constants.NON_ASSIGNED_BUGS_COUNTS;
import static telran.logs.bugs.api.Constants.OPEN;
import static telran.logs.bugs.api.Constants.PROGRAMMERS;
import static telran.logs.bugs.api.Constants.UNCLOSED_DURATION;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import telran.logs.bugs.dto.ArtifactDto;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.CloseBugData;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.OpenningMethod;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.dto.ProgrammerName;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.repo.BugRepo;
import telran.logs.bugs.jpa.repo.ProgrammerRepo;

/**
 * @author Alex Shtilman Feb 22, 2021
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureDataJpa
@TestMethodOrder(OrderAnnotation.class)
class BugsControllerTests {

	/**
	 * 
	 */
	private static final String LIMIT_INVALID = "?limit=INVALID";
	private static final String GET = "GET ";
	private static final String POST = "POST ";
	private static final String PUT = "PUT ";

	WebTestClient webClient;
	BugRepo bugRepo;
	ProgrammerRepo programmerRepo;

	@Autowired
	public BugsControllerTests(WebTestClient webClient, BugRepo bugRepo, ProgrammerRepo programmerRepo) {
		this.webClient = webClient;
		this.bugRepo = bugRepo;
		this.programmerRepo = programmerRepo;
	}

	@Test
	@Order(0)
	@DisplayName("All beans sucsessfully loaded!")
	void testContext() {
		assertNotNull(webClient);
		assertNotNull(bugRepo);
		assertNotNull(programmerRepo);
	}

	@Test
	@Order(1)
	@DisplayName("Populating test DB")
	void populateDb() {
		List<Programmer> programmers = Arrays.asList(new Programmer(1, "Sara", "sara@gmail.com"),
				new Programmer(2, "moshe", "moshe@gmail.com"), new Programmer(3, "new", "new@gmail.com"));

		programmerRepo.saveAll(programmers);

		List<Bug> bugs = Arrays.asList(
				new Bug("BLOCKING bug description", LocalDate.of(1991, 01, 01), null, BugStatus.ASSIGNED,
						Seriousness.BLOCKING, OpenningMethod.MANUAL, programmers.get(0)),
				new Bug("CRITICAL bug description", LocalDate.of(1991, 01, 01), null, BugStatus.OPEND,
						Seriousness.CRITICAL, OpenningMethod.AUTOMATIC, programmers.get(0)),
				new Bug("MINOR bug description", LocalDate.of(1991, 01, 01), LocalDate.of(2018, 01, 01),
						BugStatus.CLOSED, Seriousness.MINOR, OpenningMethod.AUTOMATIC, programmers.get(0)),
				new Bug("CRITICAL bug description", LocalDate.of(1991, 01, 01), null, BugStatus.OPEND,
						Seriousness.CRITICAL, OpenningMethod.AUTOMATIC, null),
				new Bug("MINOR bug description", LocalDate.of(1991, 01, 01), LocalDate.of(1991, 02, 01),
						BugStatus.CLOSED, Seriousness.COSMETIC, OpenningMethod.MANUAL, programmers.get(1)));
		bugRepo.saveAll(bugs);
	}

	@Test
	@Order(2)
	@DisplayName(POST + BUGS_CONTROLLER + PROGRAMMERS + " valid programmer")
	void testPostAddProgrammer() {
		ProgrammerDto expected = new ProgrammerDto(6, "Alex", "alex@gmail.com");
		testPostOkAndEqual(BUGS_CONTROLLER + PROGRAMMERS, expected, expected, ProgrammerDto.class);
	}

	@Test
	@DisplayName(POST + BUGS_CONTROLLER + PROGRAMMERS + " invalid email")
	void testPostAddProgrammerInvalidEmail() {
		testPostIsBadRequest(BUGS_CONTROLLER + PROGRAMMERS, new ProgrammerDto(8, "Alex", "ex@ist@x.0.1"));
	}

	@Test
	@DisplayName(POST + BUGS_CONTROLLER + PROGRAMMERS + " invalid name")
	void testPostAddProgrammerInvalidName() {
		testPostIsBadRequest(BUGS_CONTROLLER + PROGRAMMERS, new ProgrammerDto(8, null, "alex@gmail.com"));
	}

	@Test
	@DisplayName(POST + BUGS_CONTROLLER + PROGRAMMERS + " invalid id")
	void testPostAddProgrammerInvalidID() {
		testPostIsBadRequest(BUGS_CONTROLLER + PROGRAMMERS, new ProgrammerDto(-99, "Alex", "alex@gmail.com"));
	}

	// we don't have any implementatation yet
	@Disabled
	@Test
	@DisplayName(POST + BUGS_CONTROLLER + PROGRAMMERS + " with exsisting id")
	void testPostAddProgrammerAlreadyExist() {
		testPostIsBadRequest(BUGS_CONTROLLER + PROGRAMMERS, new ProgrammerDto(1, "Alex", "alex@gmail.com"));
	}

	@Test
	@Order(3)
	@DisplayName(POST + BUGS_CONTROLLER + OPEN + " with valid BugDto")
	void testPostOpenBug() {
		BugDto dto = new BugDto(Seriousness.BLOCKING, "Description", LocalDate.now());
		BugResponseDto expected = new BugResponseDto(dto.seriousness, dto.description, dto.dateOpen, 0, null,
				BugStatus.OPEND, OpenningMethod.MANUAL, 6);
		testPostOkAndEqual(BUGS_CONTROLLER + OPEN, dto, expected, BugResponseDto.class);
	}

	@Test
	@DisplayName(POST + BUGS_CONTROLLER + OPEN + " with null Seriousness")
	void testPostOpenBugInvalidSeriousness() {
		testPostIsBadRequest(BUGS_CONTROLLER + OPEN, new BugDto(null, "Description", LocalDate.now()));
	}

	@Test
	@DisplayName(POST + BUGS_CONTROLLER + OPEN + " with null Description")
	void testPostOpenBugInvalidDescription() {
		testPostIsBadRequest(BUGS_CONTROLLER + OPEN, new BugDto(Seriousness.BLOCKING, "", LocalDate.now()));
	}

	@Test
	@Order(4)
	@DisplayName(POST + BUGS_CONTROLLER + OPEN + ASSIGN + " with valid BugAssignDto")
	void testPostOpenAndAssignBug() {
		BugAssignDto dto = BugAssignDto.builder().dateOpen(LocalDate.now()).description("Description")
				.seriousness(Seriousness.BLOCKING).programmerId(1).build();

		BugResponseDto expected = BugResponseDto.builder().bugId(7).dateClose(null).dateOpen(dto.dateOpen)
				.description(dto.description).seriousness(dto.seriousness).status(BugStatus.ASSIGNED)
				.openningMethod(OpenningMethod.MANUAL).programmerId(1).build();

		testPostOkAndEqual(BUGS_CONTROLLER + OPEN + ASSIGN, dto, expected, BugResponseDto.class);

	}

	@Test
	@DisplayName(POST + BUGS_CONTROLLER + OPEN + ASSIGN + " with empty Description")
	void testPostOpenAndAssignInvalidBugDescription() {
		BugAssignDto invalid = BugAssignDto.builder().dateOpen(LocalDate.now()).description("")
				.seriousness(Seriousness.BLOCKING).programmerId(1).build();
		testPostIsBadRequest(BUGS_CONTROLLER + OPEN + ASSIGN, invalid);
	}

	@Test
	@DisplayName(POST + BUGS_CONTROLLER + OPEN + ASSIGN + " with null Seriosness")
	void testPostOpenAndAssignInvalidBugSeriosness() {
		BugAssignDto invalid = BugAssignDto.builder().dateOpen(LocalDate.now()).description("Description")
				.seriousness(null).programmerId(1).build();
		testPostIsBadRequest(BUGS_CONTROLLER + OPEN + ASSIGN, invalid);
	}

	@Test
	@DisplayName(POST + BUGS_CONTROLLER + OPEN + ASSIGN + " with invalid ProgrammerId")
	void testPostOpenAndAssignInvalidBugProgrammerId() {
		BugAssignDto invalid = BugAssignDto.builder().dateOpen(LocalDate.now()).description("Description")
				.seriousness(null).programmerId(-1).build();
		testPostIsBadRequest(BUGS_CONTROLLER + OPEN + ASSIGN, invalid);
	}

	// We don't have implementation yet
	@Disabled
	@Test
	@DisplayName(POST + BUGS_CONTROLLER + OPEN + ASSIGN + " with non existing ProgrammerId")
	void testPostOpenAndAssignInvalidBugProgrammerNotFound() {
		BugAssignDto invalid = BugAssignDto.builder().dateOpen(LocalDate.now()).description("Description")
				.seriousness(null).programmerId(999).build();
		testPostIsBadRequest(BUGS_CONTROLLER + OPEN + ASSIGN, invalid);
	}

	@Test
	@Order(5)
	@DisplayName(PUT + BUGS_CONTROLLER + ASSIGN + " with valid AssignBugData")
	void testPutAndAssignBug() {
		AssignBugData dto = new AssignBugData(4, 1, "assigned!");
		webClient.put().uri(BUGS_CONTROLLER + ASSIGN).contentType(MediaType.APPLICATION_JSON).bodyValue(dto).exchange()
				.expectStatus().isOk();
	}

	@Test
	@DisplayName(PUT + BUGS_CONTROLLER + ASSIGN + " with invalid bugId")
	void testPutAndAssignInvalidBugId() {
		testPutIsBadRequest(BUGS_CONTROLLER + ASSIGN, new AssignBugData(-1, 1, "assigned!"));
	}

	@Test
	@DisplayName(PUT + BUGS_CONTROLLER + ARTIFACTS)
	@Order(6)
	void testAddArtifact() {
		ArtifactDto drtifactDto = new ArtifactDto("Artifact №42", 1);
		testPostOkAndEqual(BUGS_CONTROLLER + ARTIFACTS, drtifactDto, drtifactDto, ArtifactDto.class);
	}

	@Test
	@DisplayName(PUT + BUGS_CONTROLLER + ARTIFACTS + " with invalid ArtifacId")
	void testAddArtifactInvalidID() {
		ArtifactDto drtifactDto = new ArtifactDto("", 1);
		testPostIsBadRequest(BUGS_CONTROLLER + ARTIFACTS, drtifactDto);
	}

	@Test
	@DisplayName(PUT + BUGS_CONTROLLER + ARTIFACTS + " with invalid ProgrammerId")
	void testAddArtifactInvalidProgrammer() {
		ArtifactDto drtifactDto = new ArtifactDto("Artifact №42", -1);
		testPostIsBadRequest(BUGS_CONTROLLER + ARTIFACTS, drtifactDto);
	}

	// We don't have implementation yet
	@Disabled
	@Test
	@DisplayName(PUT + BUGS_CONTROLLER + ARTIFACTS + " with not found ProgrammerId")
	void testAddArtifactNotFoundProgrammer() {
		ArtifactDto drtifactDto = new ArtifactDto("Artifact №42", 999);
		testPostIsBadRequest(BUGS_CONTROLLER + ARTIFACTS, drtifactDto);
	}

	@Test
	@DisplayName(PUT + BUGS_CONTROLLER + ASSIGN + " with invalid ProgrammerId")
	void testPutAndAssignInvalidProgrammerId() {
		testPutIsBadRequest(BUGS_CONTROLLER + ASSIGN, new AssignBugData(4, -1, "assigned!"));
	}

	// We don't have implementation yet
	@Disabled
	@Test
	@DisplayName(PUT + BUGS_CONTROLLER + ASSIGN + " with non exist ProgrammerId")
	void testPutAndAssignInvalidProgrammerNotExist() {
		testPutIsBadRequest(BUGS_CONTROLLER + ASSIGN, new AssignBugData(4, 999, "assigned!"));
	}

	@Test
	@Order(6)
	@DisplayName(PUT + BUGS_CONTROLLER + CLOSE + " with valid CloseBugData")
	void testcloseBug() {
		CloseBugData dto = new CloseBugData(1, LocalDate.now(), "description");
		webClient.put().uri(BUGS_CONTROLLER + CLOSE).contentType(MediaType.APPLICATION_JSON).bodyValue(dto).exchange()
				.expectStatus().isOk();
	}

	@Test
	@DisplayName(PUT + BUGS_CONTROLLER + CLOSE + " with invalid bugId")
	void testcloseBugInvalidId() {
		testPutIsBadRequest(BUGS_CONTROLLER + CLOSE, new CloseBugData(-1, LocalDate.now(), "description"));
	}

	// We don't have implementation yet
	@Disabled
	@Test
	@DisplayName(PUT + BUGS_CONTROLLER + CLOSE + " with bug already closed")
	void testcloseBugAlreadyClosed() {
		testPutIsBadRequest(BUGS_CONTROLLER + CLOSE, new CloseBugData(1, LocalDate.now(), "description"));
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + PROGRAMMERS + "/1")
	void testGetProgrammersBugsById() {
		List<BugResponseDto> expected = Arrays.asList(
				new BugResponseDto(Seriousness.BLOCKING, "bug was closed 2021-02-26 because: description",
						LocalDate.of(1991, 1, 1), 1, LocalDate.now(), BugStatus.CLOSED, OpenningMethod.MANUAL, 1),
				new BugResponseDto(Seriousness.CRITICAL, "CRITICAL bug description", LocalDate.of(1991, 1, 1), 1, null,
						BugStatus.OPEND, OpenningMethod.AUTOMATIC, 2),
				new BugResponseDto(Seriousness.MINOR, "MINOR bug description", LocalDate.of(1991, 1, 1), 1,
						LocalDate.of(2018, 1, 1), BugStatus.CLOSED, OpenningMethod.AUTOMATIC, 3),
				new BugResponseDto(Seriousness.CRITICAL, "CRITICAL bug description%n Assigment Description assigned!",
						LocalDate.of(1991, 1, 1), 1, null, BugStatus.ASSIGNED, OpenningMethod.AUTOMATIC, 4),
				new BugResponseDto(Seriousness.BLOCKING, "Description", LocalDate.now(), 1, null, BugStatus.ASSIGNED,
						OpenningMethod.MANUAL, 7));
		testGetOkAndEqual(BUGS_CONTROLLER + PROGRAMMERS + "/1", BugResponseDto.class, expected);
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + PROGRAMMERS + "/Vasya")
	void testGetProgrammersBugsByInvalidId() {
		webClient.get().uri(BUGS_CONTROLLER + PROGRAMMERS + "/Vasya").exchange().expectStatus().isBadRequest();
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + PROGRAMMERS + "/999")
	void testGetProgrammersBugsByNonExistId() {
		testGetOkAndEqual(BUGS_CONTROLLER + PROGRAMMERS + "/999", BugResponseDto.class, Collections.emptyList());
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + EMAIL_BUGS_COUNTS)
	void testGetEmailBugsCounts() {
		List<EmailBugsCountTest> expected = Arrays.asList(new EmailBugsCountTest("sara@gmail.com", 5),
				new EmailBugsCountTest("moshe@gmail.com", 1), new EmailBugsCountTest("new@gmail.com", 0),
				new EmailBugsCountTest("alex@gmail.com", 0));

		testGetOkAndEqual(BUGS_CONTROLLER + EMAIL_BUGS_COUNTS, EmailBugsCountTest.class, expected);
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + NON_ASSIGNED_BUGS_COUNTS)
	void testGetNonAssignedBugs() {
		List<BugResponseDto> expected = Arrays.asList(
				new BugResponseDto(Seriousness.CRITICAL, "CRITICAL bug description", LocalDate.of(1991, 1, 1), 1, null,
						BugStatus.OPEND, OpenningMethod.AUTOMATIC, 2),
				new BugResponseDto(Seriousness.BLOCKING, "Description", LocalDate.now(), 0, null, BugStatus.OPEND,
						OpenningMethod.MANUAL, 6));
		testGetOkAndEqual(BUGS_CONTROLLER + NON_ASSIGNED_BUGS_COUNTS, BugResponseDto.class, expected);
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + UNCLOSED_DURATION + "?days=1")
	void testGetUnclosedBugsMoreDuration() {
		List<BugResponseDto> expected = Arrays.asList(
				new BugResponseDto(Seriousness.CRITICAL, "CRITICAL bug description", LocalDate.of(1991, 1, 1), 0, null,
						BugStatus.OPEND, OpenningMethod.AUTOMATIC, 2),
				new BugResponseDto(Seriousness.CRITICAL, "CRITICAL bug description%n Assigment Description assigned!",
						LocalDate.of(1991, 1, 1), 1, null, BugStatus.ASSIGNED, OpenningMethod.AUTOMATIC, 4));
		testGetOkAndEqual(BUGS_CONTROLLER + UNCLOSED_DURATION + "?days=1", BugResponseDto.class, expected);
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + UNCLOSED_DURATION)
	void testGetUnclosedBugsMoreDurationNoDuration() {
		testGetIsBadRequest(BUGS_CONTROLLER + UNCLOSED_DURATION);
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + UNCLOSED_DURATION + "?days=INVALID")
	void testGetUnclosedBugsMoreDurationInvalidDuration() {
		testGetIsBadRequest(BUGS_CONTROLLER + UNCLOSED_DURATION + "?days=INVALID");
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + MOST_BUGS + "?limit=2")
	void testGetProgrammersMostBugs() {
		List<ProgrammerNameTest> expected = Arrays.asList(new ProgrammerNameTest("Sara"),
				new ProgrammerNameTest("moshe"));
		testGetOkAndEqual(BUGS_CONTROLLER + MOST_BUGS + "?limit=2", ProgrammerNameTest.class, expected);
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + MOST_BUGS)
	void testGetProgrammersMostBugsNoLimit() {
		testGetIsBadRequest(BUGS_CONTROLLER + MOST_BUGS);
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + MOST_BUGS + LIMIT_INVALID)
	void testGetProgrammersMostBugsInvalidLimit() {
		testGetIsBadRequest(BUGS_CONTROLLER + MOST_BUGS + LIMIT_INVALID);
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + LEAST_BUGS + "?limit=2")
	void testGetProgrammersLeastBugs() {
		List<ProgrammerNameTest> expected = Arrays.asList(new ProgrammerNameTest("Alex"),
				new ProgrammerNameTest("new"));
		testGetOkAndEqual(BUGS_CONTROLLER + LEAST_BUGS + "?limit=2", ProgrammerNameTest.class, expected);
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + LEAST_BUGS)
	void testGetProgrammersLeastNoLimit() {
		testGetIsBadRequest(BUGS_CONTROLLER + LEAST_BUGS);
	}

	@Test
	@DisplayName(GET + BUGS_CONTROLLER + LEAST_BUGS + LIMIT_INVALID)
	void testGetProgrammersLeastInvalidLimit() {
		testGetIsBadRequest(BUGS_CONTROLLER + LEAST_BUGS + LIMIT_INVALID);
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@EqualsAndHashCode
	static class EmailBugsCountTest implements EmailBugsCount {
		private String email;
		private long count;

		@Override
		public String getEmail() {
			return email;
		}

		@Override
		public long getCount() {
			return count;
		}

	}

	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@EqualsAndHashCode
	static class ProgrammerNameTest implements ProgrammerName {
		String name;

		@Override
		public String getName() {
			return name;
		}

	}

	public <T> void testGetOkAndEqual(String uri, Class<T> dtoResponceClazz, List<T> dtoResponce) {
		webClient.get().uri(uri).exchange().expectStatus().isOk().expectBodyList(dtoResponceClazz)
				.isEqualTo(dtoResponce);
	}

	public <T> void testPostIsBadRequest(String uri, T invalidDto) {
		getResponceFromPost(uri, invalidDto).expectStatus().isBadRequest();
	}

	public void testGetIsBadRequest(String uri) {
		webClient.get().uri(uri).exchange().expectStatus().isBadRequest();
	}

	public <T> void testPutIsBadRequest(String uri, T dto) {
		webClient.put().uri(uri).contentType(MediaType.APPLICATION_JSON).bodyValue(dto).exchange().expectStatus()
				.isBadRequest();
	}

	public <T> void testPostOkAndEqual(String uri, Class<T> dtoResponceClazz, T dtoRequest) {
		expectOkAndEqualFromPost(uri, dtoRequest, dtoRequest, dtoResponceClazz);
	}

	public <T, P> void testPostOkAndEqual(String uri, T dtoRequest, P dtoResponce, Class<P> dtoResponceClazz) {
		expectOkAndEqualFromPost(uri, dtoRequest, dtoResponce, dtoResponceClazz);
	}

	public <T, P> void expectOkAndEqualFromPost(String uri, T dtoRequest, P dtoResponce, Class<P> dtoResponceClazz) {
		getResponceFromPost(uri, dtoRequest).expectStatus().isOk().expectBody(dtoResponceClazz).isEqualTo(dtoResponce);
	}

	public <T> ResponseSpec getResponceFromPost(String uri, T dtoRequest) {
		return webClient.post().uri(uri).contentType(MediaType.APPLICATION_JSON).bodyValue(dtoRequest).exchange();
	}

}
