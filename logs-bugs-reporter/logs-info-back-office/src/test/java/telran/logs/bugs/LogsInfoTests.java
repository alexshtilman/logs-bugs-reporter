package telran.logs.bugs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.components.RandomLogsComponent;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.repo.LogRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@EnableAutoConfiguration
@AutoConfigureDataMongo
@Log4j2
class LogsInfoTests {
	@Value("${logs-exceptions-by-type:100}")
	int EXCEPTIONS_COUNT_BY_TYPE;
	@Value("${logs-exceptions-all:100}")
	int EXCEPTIONS_COUNT;
	@Value("${logs-all:100}")
	int ALL_EXCEPTIONS_COUNT;

	@Value("${logs-all:100000}")
	int MAX_LOG_COUNT;

	@Autowired
	private WebTestClient webClient;

	@Autowired
	RandomLogsComponent randomlogs;

	@Autowired
	LogRepository logRepo;

	@Test
	@Order(1)
	void fillDb() {
		List<LogDoc> logs = Stream.generate(() -> new LogDoc(randomlogs.createRandomLog())).parallel()
				.limit(MAX_LOG_COUNT).collect(Collectors.toList());
		logRepo.saveAll(logs).buffer().blockFirst();
		log.debug("Saved {} logs", logs.size());
		assertEquals(MAX_LOG_COUNT, logRepo.count().block());
	}

	@Test
	@Order(2)
	void testAllLogs() {
		List<LogDto> result = webClient.get().uri("/logs/all").exchange().expectStatus().isOk()
				.returnResult(LogDto.class).getResponseBody().take(ALL_EXCEPTIONS_COUNT).collectList().block();
		assertFalse(result.isEmpty());
		assertEquals(EXCEPTIONS_COUNT_BY_TYPE, result.size());
	}

	@Test
	@Order(3)
	void testByType() {

		for (LogType type : Arrays.asList(LogType.values())) {
			List<LogDto> result = webClient.get().uri("/logs/by_type?type=" + type.name()).exchange().expectStatus()
					.isOk().returnResult(LogDto.class).getResponseBody().take(EXCEPTIONS_COUNT_BY_TYPE).collectList()
					.block();
			assertEquals(EXCEPTIONS_COUNT_BY_TYPE, result.size());
			assertFalse(result.isEmpty());
			log.debug("recived {} LogDto to test on {}", result.size(), type.name());
			assertThat(result).allSatisfy(dto -> assertEquals(type, dto.logType));
		}

	}

	@Test
	void testExceptions() {
		List<LogDto> result = webClient.get().uri("/logs/exceptions").exchange().expectStatus().isOk()
				.returnResult(LogDto.class).getResponseBody().take(EXCEPTIONS_COUNT).collectList().block();
		assertFalse(result.isEmpty());
		assertEquals(EXCEPTIONS_COUNT_BY_TYPE, result.size());
		assertThat(result).allSatisfy(dto -> assertNotEquals(LogType.NO_EXCEPTION, dto.logType));
	}
}
