package telran.logs.bugs;

import static telran.logs.bugs.api.Constants.ASSIGN;
import static telran.logs.bugs.api.Constants.BUGS_CONTROLLER;
import static telran.logs.bugs.api.Constants.EMAIL_BUGS_COUNTS;
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
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.OpenningMethod;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.dto.Seriousness;

/**
 * @author Alex Shtilman Feb 22, 2021
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureDataJpa
class BugsControllerTests {

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

	@Autowired
	WebTestClient webClient;

	@Test
	void testCount() {
		List<EmailBugsCountTest> expected = new ArrayList<>();
		expected.add(new EmailBugsCountTest("sara@gmail.com", 3));
		expected.add(new EmailBugsCountTest("moshe@gmail.com", 1));
		expected.add(new EmailBugsCountTest("new@gmail.com", 0));
		webClient.get().uri(BUGS_CONTROLLER + EMAIL_BUGS_COUNTS).exchange().expectStatus().isOk()
				.expectBodyList(EmailBugsCountTest.class).isEqualTo(expected);
	}

	@Test
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
	void testPostAddProgrammer() {
		ProgrammerDto expected = new ProgrammerDto(6, "Alex", "alex@gmail.com");
		ProgrammerDto invalid = new ProgrammerDto(-1, null, "ex@ist@x.0.1");

		testAssertions(BUGS_CONTROLLER + PROGRAMMERS, ProgrammerDto.class, expected, invalid);

	}

	@Test
	void testPostOpenBug() {

		BugDto dto = new BugDto(Seriousness.BLOCKING, "Description", LocalDate.now());
		BugResponseDto expected = new BugResponseDto(dto.seriousness, dto.description, dto.dateOpen, 0, null,
				BugStatus.OPEND, OpenningMethod.MANUAL, 6);

		BugDto invalid = new BugDto(null, "", null);
		testAssertions(BUGS_CONTROLLER + OPEN, BugResponseDto.class, dto, expected, invalid);

	}

	@Test
	void testPostOpenAndAssignBug() {
		BugAssignDto dto = BugAssignDto.builder().dateOpen(LocalDate.now()).description("Description")
				.seriousness(Seriousness.BLOCKING).programmerId(1).build();

		BugResponseDto expected = BugResponseDto.builder().bugId(6).dateClose(null).dateOpen(dto.dateOpen)
				.description(dto.description).seriousness(dto.seriousness).status(BugStatus.ASSIGNED)
				.openningMethod(OpenningMethod.MANUAL).programmerId(1).build();

		BugAssignDto invalid = new BugAssignDto(null, null, null, 0);
		testAssertions(BUGS_CONTROLLER + OPEN + ASSIGN, BugResponseDto.class, dto, expected, invalid);

	}

	@Test
	void testPutAndAssignBug() {
		AssignBugData dto = new AssignBugData(4, 1, "assigned!");

		webClient.put().uri(BUGS_CONTROLLER + ASSIGN).contentType(MediaType.APPLICATION_JSON).bodyValue(dto).exchange()
				.expectStatus().isOk();

	}

	public <T> ResponseSpec getResponceFromPost(String uri, T bodyValue) {
		return webClient.post().uri(uri).contentType(MediaType.APPLICATION_JSON).bodyValue(bodyValue).exchange();
	}

	public <T, P> void expectOkAndEqual(String uri, T dtoRequest, P dtoResponce, Class<P> dtoResponceClazz) {
		getResponceFromPost(uri, dtoRequest).expectStatus().isOk().expectBody(dtoResponceClazz).isEqualTo(dtoResponce);
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

}
