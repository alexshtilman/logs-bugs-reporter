package telran.logs.bugs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.List;

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
import reactor.core.publisher.Flux;
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
		logRepo.saveAll(Flux.create(sink -> {
			for (int i = 0; i < MAX_LOG_COUNT; i++) {
				sink.next(new LogDoc(randomlogs.createRandomLog()));
			}
			sink.complete();
		})).blockLast();
		log.debug("Saved {} logs", MAX_LOG_COUNT);
		assertEquals(MAX_LOG_COUNT, logRepo.count().block());
	}

	@Test
	void testTakeFirstFromAllLogs() {
		getFromRestAndAssert("/logs/all", ALL_EXCEPTIONS_COUNT);
	}

	@Test

	void testTakeFirstByType() {
		for (LogType type : Arrays.asList(LogType.values())) {
			assertThat(getFromRestAndAssert("/logs/by_type?type=" + type.name(), EXCEPTIONS_COUNT))
					.allSatisfy(dto -> assertEquals(type, dto.logType));
		}
	}

	@Test
	void testTakeFirstFromExceptions() {
		assertThat(getFromRestAndAssert("/logs/exceptions", EXCEPTIONS_COUNT_BY_TYPE))
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
