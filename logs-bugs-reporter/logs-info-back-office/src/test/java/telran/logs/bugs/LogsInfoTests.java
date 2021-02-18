package telran.logs.bugs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
	private static final int ALL_EXCEPTIONS_COUNT = 20;
	private static final int EXCEPTIONS_COUNT = 6;
	private static final int EXCEPTIONS_COUNT_BY_TYPE = 1;

	private static final String LOGS = "/logs";
	private static final String EXCEPTIONS = "/exceptions";
	private static final String BY_TYPE = "/by_type?type=";
	private static final String ALL = "/all";

	private static final String STATISTICS = "/statistics";
	private static final String ARTIFACT_AND_COUNT = "/artifact_and_count";
	private static final String MOST_ENCOUNTERED_ARTIFACTS = "/most_encountered_artifacts";
	private static final String MOST_ENCOUNTERED_EXCEPTIONS = "/most_encountered_exceptions";
	private static final String LOGTYPE_AND_COUNT = "/logtype_and_count";

	@Autowired
	private WebTestClient webClient;

	@Autowired
	LogRepository logRepo;

	@Test
	@Order(1)
	void fillDb() {
		List<LogDoc> docs = new ArrayList<>();
		for (LogType type : LogType.values()) {
			docs.add(new LogDoc(new Date(), type, "artifact", 0, ""));
		}
		for (int i = 0; i < 3; i++) {
			docs.add(new LogDoc(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact1", 0, ""));
			docs.add(new LogDoc(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact2", 0, ""));
			docs.add(new LogDoc(new Date(), LogType.NOT_FOUND_EXCEPTION, "artifact2", 0, ""));
			docs.add(new LogDoc(new Date(), LogType.SERVER_EXCEPTION, "artifact2", 0, ""));
		}
		docs.add(new LogDoc(new Date(), LogType.NO_EXCEPTION, "", 100, ""));
		logRepo.saveAll(docs).blockLast();
		log.debug("Saved {} logs", ALL_EXCEPTIONS_COUNT);
		assertEquals(ALL_EXCEPTIONS_COUNT, logRepo.count().block());
	}

	@Nested
	class LogTests {

		@Test
		void testTakeFirstFromAllLogs() {
			getFromRestAndAssert(LOGS + ALL, EXCEPTIONS_COUNT);
		}

		@Test
		void testTakeFirstByType() {
			for (LogType type : Arrays.asList(LogType.values())) {
				assertThat(getFromRestAndAssert(LOGS + BY_TYPE + type.name(), EXCEPTIONS_COUNT_BY_TYPE))
						.allSatisfy(dto -> assertEquals(type, dto.logType));
			}
		}

		@Test
		void testWrongType() {
			webClient.get().uri(LOGS + BY_TYPE + "NOT_EXIST").exchange().expectStatus().isBadRequest();
		}

		@Test
		void testTakeFirstFromExceptions() {
			assertThat(getFromRestAndAssert(LOGS + EXCEPTIONS, EXCEPTIONS_COUNT))
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
			expected.add(new LogTypeAndCountDto(LogType.AUTHENTICATION_EXCEPTION, 7));
			expected.add(new LogTypeAndCountDto(LogType.SERVER_EXCEPTION, 4));
			expected.add(new LogTypeAndCountDto(LogType.NOT_FOUND_EXCEPTION, 4));
			expected.add(new LogTypeAndCountDto(LogType.NO_EXCEPTION, 2));
			expected.add(new LogTypeAndCountDto(LogType.AUTHORIZATION_EXCEPTION, 1));
			expected.add(new LogTypeAndCountDto(LogType.BAD_REQUEST_EXCEPTION, 1));
			expected.add(new LogTypeAndCountDto(LogType.DUPLICATED_KEY_EXCEPTION, 1));

			webClient.get().uri(STATISTICS + LOGTYPE_AND_COUNT).exchange().expectStatus().isOk()
					.expectBody(new ParameterizedTypeReference<List<LogTypeAndCountDto>>() {
					}).isEqualTo(expected);

		}

		@Test
		void testGetFirstMostEncounteredExceptions() {
			List<LogType> expected = new ArrayList<>();
			expected.add(LogType.AUTHENTICATION_EXCEPTION);
			expected.add(LogType.SERVER_EXCEPTION);
			expected.add(LogType.NOT_FOUND_EXCEPTION);

			webClient.get().uri(STATISTICS + MOST_ENCOUNTERED_EXCEPTIONS).exchange().expectStatus().isOk()
					.expectBody(new ParameterizedTypeReference<List<LogType>>() {
					}).isEqualTo(expected);

		}

		@Test
		void testGetFirstMostEncounteredArtifacts() {
			List<String> expected = new ArrayList<>();
			expected.add("artifact2");
			expected.add("artifact");
			expected.add("artifact1");

			webClient.get().uri(STATISTICS + MOST_ENCOUNTERED_ARTIFACTS).exchange().expectStatus().isOk()
					.expectBody(new ParameterizedTypeReference<List<String>>() {
					}).isEqualTo(expected);
		}

		@Test
		void testGetArtifactOccuresnces() {
			List<ArtifactAndCountDto> expected = new ArrayList<>();
			expected.add(new ArtifactAndCountDto("artifact2", 9));
			expected.add(new ArtifactAndCountDto("artifact", 7));
			expected.add(new ArtifactAndCountDto("artifact1", 3));
			expected.add(new ArtifactAndCountDto("", 1));

			webClient.get().uri(STATISTICS + ARTIFACT_AND_COUNT).exchange().expectStatus().isOk()
					.expectBody(new ParameterizedTypeReference<List<ArtifactAndCountDto>>() {
					}).isEqualTo(expected);

		}

		<T> void getListFromUriAndAssert(String uri, List<T> expected) {
			webClient.get().uri(uri).exchange().expectStatus().isOk()
					.expectBody(new ParameterizedTypeReference<List<T>>() {
					}).isEqualTo(expected);
			// this
		}
	}
}
