package telran.logs.bugs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static telran.logs.bugs.api.Constants.ALL;
import static telran.logs.bugs.api.Constants.ARTIFACT_AND_COUNT;
import static telran.logs.bugs.api.Constants.BY_TYPE;
import static telran.logs.bugs.api.Constants.EXCEPTIONS;
import static telran.logs.bugs.api.Constants.LOGS_CONTROLLER;
import static telran.logs.bugs.api.Constants.LOGTYPE_AND_COUNT;
import static telran.logs.bugs.api.Constants.MOST_ENCOUNTERED_ARTIFACTS;
import static telran.logs.bugs.api.Constants.MOST_ENCOUNTERED_EXCEPTIONS;
import static telran.logs.bugs.api.Constants.STATISTICS_CONTROLLER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.ArtifactAndCountDto;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.LogTypeAndCountDto;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.repo.LogRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@EnableAutoConfiguration
@AutoConfigureDataMongo
@Log4j2
class LogsInfoTests {
	private static final int ALL_EXCEPTIONS_COUNT = 8;
	private static final int EXCEPTIONS_COUNT = 6;
	private static final int EXCEPTIONS_COUNT_BY_TYPE = 1;

	WebTestClient webClient;
	LogRepository logRepo;

	@Autowired
	public LogsInfoTests(WebTestClient webClient, LogRepository logRepo) {
		super();
		this.webClient = webClient;
		this.logRepo = logRepo;
	}

	@Test
	@Order(1)
	void fillDb() {
		List<LogDoc> docs = new ArrayList<>();
		for (LogType type : LogType.values()) {
			docs.add(new LogDoc(new Date(), type, "artifact", 0, ""));
		}
		docs.add(new LogDoc(new Date(), LogType.NO_EXCEPTION, "NO_EXCEPTION", 100, ""));
		logRepo.saveAll(docs).blockLast();
		log.debug("Saved {} logs", ALL_EXCEPTIONS_COUNT);
		assertEquals(ALL_EXCEPTIONS_COUNT, logRepo.count().block());
	}

	@Nested
	class LogTests {

		@Test
		void testTakeFirstFromAllLogs() {
			getFromRestAndAssert(LOGS_CONTROLLER + ALL, EXCEPTIONS_COUNT);
		}

		@Test
		void testTakeFirstByType() {
			for (LogType type : Arrays.asList(LogType.values())) {
				assertThat(getFromRestAndAssert(LOGS_CONTROLLER + BY_TYPE + "?type=" + type.name(),
						EXCEPTIONS_COUNT_BY_TYPE)).allSatisfy(dto -> assertEquals(type, dto.logType));
			}
		}

		@Test
		void testWrongType() {
			webClient.get().uri(LOGS_CONTROLLER + BY_TYPE + "?type=NOT_EXIST").exchange().expectStatus()
					.is5xxServerError();
		}

		@Test
		void testTakeFirstFromExceptions() {
			assertThat(getFromRestAndAssert(LOGS_CONTROLLER + EXCEPTIONS, EXCEPTIONS_COUNT))
					.allSatisfy(dto -> assertNotEquals(LogType.NO_EXCEPTION, dto.logType));
		}

		public List<LogDto> getFromRestAndAssert(String uri, int count) {
			List<LogDto> result = webClient.get().uri(uri).exchange().expectStatus().isOk().returnResult(LogDto.class)
					.getResponseBody().take(count).collectList().block();
			log.debug("recived {} LogDto", result.size());
			assertFalse(result.isEmpty());
			assertEquals(count, result.size());
			return result;

		}
	}

	@Nested
	class StatisticsTests {

		@Test
		void testGetLogTypeOccurences() {
			List<LogTypeAndCountDto> expected = new ArrayList<>();
			expected.add(new LogTypeAndCountDto(LogType.NO_EXCEPTION, 2));
			expected.add(new LogTypeAndCountDto(LogType.AUTHORIZATION_EXCEPTION, 1));
			expected.add(new LogTypeAndCountDto(LogType.SERVER_EXCEPTION, 1));
			expected.add(new LogTypeAndCountDto(LogType.AUTHENTICATION_EXCEPTION, 1));
			expected.add(new LogTypeAndCountDto(LogType.BAD_REQUEST_EXCEPTION, 1));
			expected.add(new LogTypeAndCountDto(LogType.DUPLICATED_KEY_EXCEPTION, 1));
			expected.add(new LogTypeAndCountDto(LogType.NOT_FOUND_EXCEPTION, 1));

			getListFromUriAndAssert(STATISTICS_CONTROLLER + LOGTYPE_AND_COUNT, expected,
					new ParameterizedTypeReference<List<LogTypeAndCountDto>>() {
					});
		}

		@Test
		void testGetFirstMostEncounteredExceptions() {
			List<LogType> expected = new ArrayList<>();
			expected.add(LogType.AUTHORIZATION_EXCEPTION);
			expected.add(LogType.AUTHENTICATION_EXCEPTION);
			expected.add(LogType.BAD_REQUEST_EXCEPTION);

			getListFromUriAndAssert(STATISTICS_CONTROLLER + MOST_ENCOUNTERED_EXCEPTIONS, expected,
					new ParameterizedTypeReference<List<LogType>>() {
					});
		}

		@Test
		void testGetFirstMostEncounteredArtifacts() {
			List<String> expected = new ArrayList<>();
			expected.add("artifact");
			expected.add("NO_EXCEPTION");

			getListFromUriAndAssert(STATISTICS_CONTROLLER + MOST_ENCOUNTERED_ARTIFACTS, expected,
					new ParameterizedTypeReference<List<String>>() {
					});
		}

		@Test
		void testGetArtifactOccuresnces() {
			List<ArtifactAndCountDto> expected = new ArrayList<>();
			expected.add(new ArtifactAndCountDto("artifact", 7));
			expected.add(new ArtifactAndCountDto("NO_EXCEPTION", 1));

			getListFromUriAndAssert(STATISTICS_CONTROLLER + ARTIFACT_AND_COUNT, expected,
					new ParameterizedTypeReference<List<ArtifactAndCountDto>>() {
					});
		}

		<T> void getListFromUriAndAssert(String uri, List<T> expected,
				ParameterizedTypeReference<List<T>> typeReference) {
			webClient.get().uri(uri).exchange().expectStatus().isOk().expectBody(typeReference).isEqualTo(expected);
		}
	}
}
