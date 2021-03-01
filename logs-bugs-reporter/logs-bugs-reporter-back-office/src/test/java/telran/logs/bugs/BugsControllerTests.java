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
import static telran.logs.bugs.api.Constants.SERIOSNESS_BUGS_COUNT;
import static telran.logs.bugs.api.Constants.TYPES_BUGS_COUNT;
import static telran.logs.bugs.api.Constants.UNCLOSED_DURATION;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.StatusAssertions;
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
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.dto.SeriousnessBugCount;
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

	private static final String LIMIT_INVALID = "?limit=-42";

	enum Method {
		POST, PUT
	}

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
	void add_Programmer() {
		ProgrammerDto expected = new ProgrammerDto(6, "Alex", "alex@gmail.com");
		testPostOkAndEqual(BUGS_CONTROLLER + PROGRAMMERS, expected, expected, ProgrammerDto.class);
		testPostIsBadRequest(BUGS_CONTROLLER + PROGRAMMERS, new ProgrammerDto(8, "Alex", "ex@ist@x.0.1"));
	}

	@Test
	@Order(3)
	void add_Bug() {
		BugDto dto = new BugDto(Seriousness.BLOCKING, "Description", LocalDate.now());
		BugResponseDto expected = new BugResponseDto(dto.seriousness, dto.description, dto.dateOpen, 0, null,
				BugStatus.OPEND, OpenningMethod.MANUAL, 6);
		testPostOkAndEqual(BUGS_CONTROLLER + OPEN, dto, expected, BugResponseDto.class);
		testPostIsBadRequest(BUGS_CONTROLLER + OPEN, new BugDto(null, "Description", LocalDate.now()));
	}

	@Test
	@Order(4)
	void add_assigned_Bug() {
		BugAssignDto dto = BugAssignDto.builder().dateOpen(LocalDate.now()).description("Description")
				.seriousness(Seriousness.BLOCKING).programmerId(1).build();

		BugResponseDto expected = BugResponseDto.builder().bugId(7).dateClose(null).dateOpen(dto.dateOpen)
				.description(dto.description).seriousness(dto.seriousness).status(BugStatus.ASSIGNED)
				.openningMethod(OpenningMethod.MANUAL).programmerId(1).build();

		BugAssignDto invalidDescription = BugAssignDto.builder().dateOpen(LocalDate.now()).description("")
				.seriousness(Seriousness.BLOCKING).programmerId(1).build();

		BugAssignDto nonExistProgrammer = BugAssignDto.builder().dateOpen(LocalDate.now()).description("Description")
				.seriousness(Seriousness.BLOCKING).programmerId(99).build();
		testPostOkAndEqual(BUGS_CONTROLLER + OPEN + ASSIGN, dto, expected, BugResponseDto.class);
		testPostOrPutFail(Method.POST, HttpStatus.BAD_REQUEST, BUGS_CONTROLLER + OPEN + ASSIGN, invalidDescription);
		testPostOrPutFail(Method.POST, HttpStatus.NOT_FOUND, BUGS_CONTROLLER + OPEN + ASSIGN, nonExistProgrammer);
	}

	@Test
	@Order(5)
	void update_assigned_Bug() {
		testPutOk(BUGS_CONTROLLER + ASSIGN, new AssignBugData(4, 1, "assigned!"));
		testPostOrPutFail(Method.PUT, HttpStatus.BAD_REQUEST, BUGS_CONTROLLER + ASSIGN,
				new AssignBugData(-1, 1, "assigned!"));
		testPostOrPutFail(Method.PUT, HttpStatus.NOT_FOUND, BUGS_CONTROLLER + ASSIGN,
				new AssignBugData(999, 1, "assigned!"));
	}

	@Test
	@Order(6)
	void add_artifact() {
		ArtifactDto drtifactDto = new ArtifactDto("Artifact №42", 1);
		testPostOkAndEqual(BUGS_CONTROLLER + ARTIFACTS, drtifactDto, drtifactDto, ArtifactDto.class);
		testPostOrPutFail(Method.POST, HttpStatus.BAD_REQUEST, BUGS_CONTROLLER + ARTIFACTS, new ArtifactDto("", 1));
		testPostOrPutFail(Method.POST, HttpStatus.BAD_REQUEST, BUGS_CONTROLLER + ARTIFACTS,
				new ArtifactDto("Artifact", -1));
		testPostOrPutFail(Method.POST, HttpStatus.NOT_FOUND, BUGS_CONTROLLER + ARTIFACTS,
				new ArtifactDto("Artifact", 99));
	}

	@Test
	@Order(7)
	void close_Bug() {
		testPutOk(BUGS_CONTROLLER + CLOSE, new CloseBugData(1, LocalDate.now(), "description"));
		testPutIsBadRequest(BUGS_CONTROLLER + CLOSE, new CloseBugData(-1, LocalDate.now(), "description"));
	}

	@Nested
	@DisplayName("Get methods")
	class GetMethods {
		private static final String LIMIT_2 = "?limit=2";

		@Test
		void programmers_BugsById_1() {
			List<BugResponseDto> expected = Arrays.asList(
					new BugResponseDto(Seriousness.BLOCKING, "bug was closed 2021-02-26 because: description",
							LocalDate.of(1991, 1, 1), 1, LocalDate.now(), BugStatus.CLOSED, OpenningMethod.MANUAL, 1),
					new BugResponseDto(Seriousness.CRITICAL, "CRITICAL bug description", LocalDate.of(1991, 1, 1), 1,
							null, BugStatus.OPEND, OpenningMethod.AUTOMATIC, 2),
					new BugResponseDto(Seriousness.MINOR, "MINOR bug description", LocalDate.of(1991, 1, 1), 1,
							LocalDate.of(2018, 1, 1), BugStatus.CLOSED, OpenningMethod.AUTOMATIC, 3),
					new BugResponseDto(Seriousness.CRITICAL,
							"CRITICAL bug description%n Assigment Description assigned!", LocalDate.of(1991, 1, 1), 1,
							null, BugStatus.ASSIGNED, OpenningMethod.AUTOMATIC, 4),
					new BugResponseDto(Seriousness.BLOCKING, "Description", LocalDate.now(), 1, null,
							BugStatus.ASSIGNED, OpenningMethod.MANUAL, 7));
			testGetOkAndEqual(BUGS_CONTROLLER + PROGRAMMERS + "/1", BugResponseDto.class, expected);
			testGetIsBadRequest(BUGS_CONTROLLER + PROGRAMMERS + "/Vasya");

		}

		@Test
		void email_BugsCounts() {
			List<EmailBugsCountTest> expected = Arrays.asList(new EmailBugsCountTest("sara@gmail.com", 5),
					new EmailBugsCountTest("moshe@gmail.com", 1), new EmailBugsCountTest("new@gmail.com", 0),
					new EmailBugsCountTest("alex@gmail.com", 0));
			testGetOkAndEqual(BUGS_CONTROLLER + EMAIL_BUGS_COUNTS, EmailBugsCountTest.class, expected);
		}

		@Test
		void non_Assigned_Bugs() {
			List<BugResponseDto> expected = Arrays.asList(
					new BugResponseDto(Seriousness.CRITICAL, "CRITICAL bug description", LocalDate.of(1991, 1, 1), 1,
							null, BugStatus.OPEND, OpenningMethod.AUTOMATIC, 2),
					new BugResponseDto(Seriousness.BLOCKING, "Description", LocalDate.now(), 0, null, BugStatus.OPEND,
							OpenningMethod.MANUAL, 6));
			testGetOkAndEqual(BUGS_CONTROLLER + NON_ASSIGNED_BUGS_COUNTS, BugResponseDto.class, expected);
		}

		@Test
		void unclosed_Bugs_More_Duration_1day() {
			List<BugResponseDto> expected = Arrays.asList(
					new BugResponseDto(Seriousness.CRITICAL, "CRITICAL bug description", LocalDate.of(1991, 1, 1), 0,
							null, BugStatus.OPEND, OpenningMethod.AUTOMATIC, 2),
					new BugResponseDto(Seriousness.CRITICAL,
							"CRITICAL bug description%n Assigment Description assigned!", LocalDate.of(1991, 1, 1), 1,
							null, BugStatus.ASSIGNED, OpenningMethod.AUTOMATIC, 4));
			testGetOkAndEqual(BUGS_CONTROLLER + UNCLOSED_DURATION + "?days=1", BugResponseDto.class, expected);
			testGetIsBadRequest(BUGS_CONTROLLER + UNCLOSED_DURATION);
		}

		@Test
		void programmers_MostBugs_limit_2() {
			String[] expected = { "Sara", "moshe" };
			testGetOkAndEqual(BUGS_CONTROLLER + MOST_BUGS + LIMIT_2, String[].class, expected);
			testGetIsBadRequest(BUGS_CONTROLLER + MOST_BUGS + LIMIT_INVALID);
		}

		@Test
		void first_2_Programmers_with_moust_Least_Bugs() {
			String[] expected = { "Alex", "new" };
			testGetOkAndEqual(BUGS_CONTROLLER + LEAST_BUGS + LIMIT_2, String[].class, expected);
			testGetIsBadRequest(BUGS_CONTROLLER + LEAST_BUGS + LIMIT_INVALID);
		}

		@Test
		void count_Bugs_group_By_seriousness() {
			List<SeriousnessBugCountTest> expected = Arrays.asList(new SeriousnessBugCountTest(Seriousness.BLOCKING, 3),
					new SeriousnessBugCountTest(Seriousness.CRITICAL, 2),
					new SeriousnessBugCountTest(Seriousness.COSMETIC, 1),
					new SeriousnessBugCountTest(Seriousness.MINOR, 1));
			testGetOkAndEqual(BUGS_CONTROLLER + SERIOSNESS_BUGS_COUNT, SeriousnessBugCountTest.class, expected);
		}

		@Test
		void getSeriousnessTypesWithMostBugs() {
			List<Seriousness> expected = Arrays.asList(Seriousness.BLOCKING, Seriousness.CRITICAL);
			testGetOkAndEqual(BUGS_CONTROLLER + TYPES_BUGS_COUNT + LIMIT_2, Seriousness.class, expected);
			testGetIsBadRequest(BUGS_CONTROLLER + TYPES_BUGS_COUNT + LIMIT_INVALID);
		}
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
	static class SeriousnessBugCountTest implements SeriousnessBugCount {
		private Seriousness seriousness;
		private long count;

		@Override
		public long getCount() {
			return count;
		}

		@Override
		public Seriousness getSeriousness() {
			return seriousness;
		}

	}

	private <T> void testPostOrPutFail(Method method, HttpStatus status, String uri, T invalidDto) {
		StatusAssertions responceStatus = method.equals(Method.POST)
				? getResponceFromPost(uri, invalidDto).expectStatus()
				: getResponceFromPut(uri, invalidDto).expectStatus();
		switch (status) {
		case BAD_REQUEST: {
			responceStatus.isBadRequest();
			break;
		}
		case NOT_FOUND: {
			responceStatus.isNotFound();
			break;
		}
		case CONFLICT: {
			responceStatus.is4xxClientError();
			break;
		}
		case INTERNAL_SERVER_ERROR: {
			responceStatus.is5xxServerError();
			break;
		}
		default: {
			throw new IllegalArgumentException("Unexpected value: " + status);

		}
		}
	}

	private <T> void testGetFail(String uri, HttpStatus status) {
		StatusAssertions responceStatus = getResponceFromGet(uri).expectStatus();
		switch (status) {
		case BAD_REQUEST: {
			responceStatus.isBadRequest();
			break;
		}
		case NOT_FOUND: {
			responceStatus.isNotFound();
			break;
		}
		case CONFLICT: {
			responceStatus.is4xxClientError();
			break;
		}
		case INTERNAL_SERVER_ERROR: {
			responceStatus.is5xxServerError();
			break;
		}
		default: {
			throw new IllegalArgumentException("Unexpected value: " + status);
		}
		}
	}

	public <T> void testGetOkAndEqual(String uri, Class<T> dtoResponceClazz, List<T> dtoResponce) {
		webClient.get().uri(uri).exchange().expectStatus().isOk().expectBodyList(dtoResponceClazz)
				.isEqualTo(dtoResponce);
	}

	public <T> void testGetOkAndEqual(String uri, Class<T> dtoResponceClazz, T dtoResponce) {
		webClient.get().uri(uri).exchange().expectStatus().isOk().expectBody(dtoResponceClazz).isEqualTo(dtoResponce);
	}

	public <T> void testPostIsBadRequest(String uri, T invalidDto) {
		getResponceFromPost(uri, invalidDto).expectStatus().isBadRequest();
	}

	public void testGetIsBadRequest(String uri) {
		webClient.get().uri(uri).exchange().expectStatus().isBadRequest();
	}

	public <T> void testPutOk(String uri, T dtoRequest) {
		getResponceFromPut(uri, dtoRequest).expectStatus().isOk();
	}

	public <T> void testPutOkAndEqual(String uri, Class<T> dtoResponceClazz, T dtoRequest) {
		getResponceFromPut(uri, dtoRequest).expectStatus().isOk().expectBody(dtoResponceClazz).isEqualTo(dtoRequest);
	}

	public <T> void testPutIsBadRequest(String uri, T dto) {
		getResponceFromPut(uri, dto).expectStatus().isBadRequest();
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

	public <T> ResponseSpec getResponceFromPut(String uri, T dtoRequest) {
		return webClient.put().uri(uri).contentType(MediaType.APPLICATION_JSON).bodyValue(dtoRequest).exchange();
	}

	public <T> ResponseSpec getResponceFromGet(String uri) {
		return webClient.get().uri(uri).exchange();
	}
}
