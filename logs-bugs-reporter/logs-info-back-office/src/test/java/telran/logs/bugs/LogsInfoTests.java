package telran.logs.bugs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Log4j2
class LogsInfoTests {
	@Value("${logs-exceptions-by-type:1000}")
	int EXCEPTIONS_COUNT_BY_TYPE;
	@Value("${logs-exceptions-all:1000}")
	int EXCEPTIONS_COUNT;
	@Value("${logs-all:1000}")
	int ALL_EXCEPTIONS_COUNT;
	@Autowired
	private WebTestClient webClient;

	@Test
	void testAllLogs() {
		List<LogDto> result = webClient.get().uri("/logs/all").exchange().expectStatus().isOk()
				.returnResult(LogDto.class).getResponseBody().take(ALL_EXCEPTIONS_COUNT).collectList().block();
		assertFalse(result.isEmpty());
		assertEquals(EXCEPTIONS_COUNT_BY_TYPE, result.size());
	}

	@Test
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
