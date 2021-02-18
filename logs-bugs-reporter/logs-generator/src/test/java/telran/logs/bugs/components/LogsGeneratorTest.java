package telran.logs.bugs.components;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

/**
 * Tests logic of generation and percent of random chances according
 * distribution
 * 
 * @since homework 64
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@PropertySource("classpath:application.properties")
@ContextConfiguration(classes = RandomLogsComponent.class)
@Log4j2
class LogsGeneratorTest {

	@Autowired
	RandomLogsComponent myLogs;

	@Value("${count.of.logs}")
	int COUNT_OF_LOGS;

	static List<LogDto> randomLogs = new ArrayList<>();

	private static final String AUTHENTICATION_ARTIFACT = "authentication";
	private static final String AUTHORIZATION_ARTIFACT = "authorization";

	@Test
	void testLogGeneration() {
		List<LogDto> logs = Stream.generate(() -> myLogs.createRandomLog()).parallel().limit(COUNT_OF_LOGS)
				.collect(Collectors.toList());
		List<LogType> otherType = new ArrayList<>();
		otherType.add(LogType.AUTHENTICATION_EXCEPTION);
		otherType.add(LogType.AUTHORIZATION_EXCEPTION);
		logs.forEach(log -> {
			switch (log.logType) {
			case AUTHENTICATION_EXCEPTION:
				assertEquals(AUTHENTICATION_ARTIFACT, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;
			case AUTHORIZATION_EXCEPTION:
				assertEquals(AUTHORIZATION_ARTIFACT, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;

			case NO_EXCEPTION:
				assertFalse(otherType.contains(log.logType));
				assertTrue(log.responseTime > 0);
				assertTrue(log.result.isEmpty());
				break;

			default:
				assertFalse(otherType.contains(log.logType));
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;

			}
		});

		Map<LogType, Long> counted = logs.stream()
				.collect(Collectors.groupingBy(LogDto::getLogType, Collectors.counting())).entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> {
					throw new IllegalStateException();
				}, LinkedHashMap::new));
		long NO_EXCEPTION = counted.get(LogType.NO_EXCEPTION);
		long EXCEPTION = COUNT_OF_LOGS - NO_EXCEPTION;
		long SECURITY = counted.get(LogType.AUTHENTICATION_EXCEPTION) + counted.get(LogType.AUTHORIZATION_EXCEPTION);
		long NON_SECURITY = EXCEPTION - SECURITY;

		assertThat(NO_EXCEPTION * 100L / COUNT_OF_LOGS).isBetween(89L, 91L);
		assertThat(EXCEPTION * 100L / COUNT_OF_LOGS).isBetween(9L, 11L);
		assertThat(SECURITY * 100L / EXCEPTION).isBetween(29L, 31L);
		assertThat(NON_SECURITY * 100L / EXCEPTION).isBetween(69L, 71L);

		assertThat(counted.get(LogType.AUTHENTICATION_EXCEPTION) * 100L / SECURITY).isBetween(69L, 71L);
		assertThat(counted.get(LogType.AUTHORIZATION_EXCEPTION) * 100L / SECURITY).isBetween(29L, 31L);

		assertThat(counted.get(LogType.BAD_REQUEST_EXCEPTION) * 100L / NON_SECURITY).isBetween(24L, 26L);
		assertThat(counted.get(LogType.NOT_FOUND_EXCEPTION) * 100L / NON_SECURITY).isBetween(24L, 26L);
		assertThat(counted.get(LogType.DUPLICATED_KEY_EXCEPTION) * 100L / NON_SECURITY).isBetween(24L, 26L);
		assertThat(counted.get(LogType.SERVER_EXCEPTION) * 100L / NON_SECURITY).isBetween(24L, 26L);

		counted.forEach((key, value) -> log.info("{}:{}", key, value));
	}

}
