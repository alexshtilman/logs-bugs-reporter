package telran.logs.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Import;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class RandomLogsImplTest {

	@Autowired
	RandomLogsImpl myLogs;

	@Autowired
	OutputDestination output;

	@Value("${count-of-logs:100000}")
	int COUNT_OF_LOGS;
	@Autowired
	StreamBridge streamBridge;

	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;

	static Logger LOG = LoggerFactory.getLogger(RandomLogsImplTest.class);

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
		counted.forEach((key, value) -> LOG.info("{}:{}", key, value));
	}

	@Test
	void sendRandomLogs() throws InterruptedException {

		Set<String> data = new HashSet<>();
		int countOfMessages = 10;
		for (int i = 0; i < countOfMessages; i++) {
			try {
				byte[] messageBytes = output.receive(Long.MAX_VALUE).getPayload();
				String messageString = new String(messageBytes);
				data.add(messageString);
				LOG.info("recived message: {}", messageString);
			} catch (Exception e) {
				LOG.warn("error on reciving message because: {}", e.getMessage());
				i--;
			}
		}
		assertEquals(countOfMessages, data.size());
	}
}
