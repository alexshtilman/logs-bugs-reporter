package telran.logs.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.interfaces.IRandomLogs;



@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RandomLogs.class)
@EnableAutoConfiguration
@TestInstance(Lifecycle.PER_CLASS)
class RandomLogsTest {


    @Autowired
    IRandomLogs myLogs;

    static List<LogDto> randomLogs = new ArrayList<>();
    static int COUNT_OF_LOGS = 100000;

    @BeforeAll
    void setup() {
	randomLogs = myLogs.generateLogs(COUNT_OF_LOGS);
    }

    @DisplayName("Depedency artifact from logType")
    @Test
    void testArtifact() {
	assertEquals(COUNT_OF_LOGS, randomLogs.size());

	List<LogDto> authentication = randomLogs.stream().filter(l -> l.artifact == "authentication")
		.collect(Collectors.toList());

	List<LogDto> authorization = randomLogs.stream().filter(l -> l.artifact == "authorization")
		.collect(Collectors.toList());

	List<LogDto> other = randomLogs.stream()
		.filter(l -> l.artifact != "authorization" && l.artifact != "authentication")
		.collect(Collectors.toList());

	for (LogDto dto : authentication) {
	    assertEquals(LogType.AUTHENTIATION_EXCEPTION, dto.logType);
	}
	for (LogDto dto : authorization) {
	    assertEquals(LogType.ATHORIZATION_EXCEPTION, dto.logType);
	}
	List<LogType> otherType = new ArrayList<>();
	otherType.add(LogType.AUTHENTIATION_EXCEPTION);
	otherType.add(LogType.ATHORIZATION_EXCEPTION);

	for (LogDto dto : other) {
	    assertFalse(otherType.contains(dto.logType));
	}
    }

    @DisplayName("Depedency responseTime from logType")
    @Test
    void testResponseTime() {
	assertEquals(COUNT_OF_LOGS, randomLogs.size());
	// if the type is NO_EXCEPTION – number greater than 0 for other cases 0
	List<LogDto> noException = randomLogs.stream().filter(l -> l.getLogType() == LogType.NO_EXCEPTION)
		.collect(Collectors.toList());
	List<LogDto> other = randomLogs.stream().filter(l -> l.getLogType() != LogType.NO_EXCEPTION)
		.collect(Collectors.toList());
	for (LogDto dto : noException) {
	    assertNotEquals(0, dto.responseTime);
	}
	for (LogDto dto : other) {
	    assertEquals(0, dto.responseTime);
	}
    }

    @DisplayName("Empty result")
    @Test
    void testLogDtoResult() {
	assertEquals(COUNT_OF_LOGS, randomLogs.size());
	for (LogDto dto : randomLogs) {
	    assertEquals("", dto.result);
	}

    }


    @AfterAll
    void printStat() {
	Map<LogType, Long> counted = randomLogs.stream()
		.collect(Collectors.groupingBy(LogDto::getLogType, Collectors.counting())).entrySet().stream()
		.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
		.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> {
		    throw new IllegalStateException();
		}, LinkedHashMap::new));
	System.out.println("Statistics using stream");
	counted.forEach((key, value) -> System.out.printf("%s:%d\n", key, value));
	System.out.println("Statistics using mongo");
	myLogs.getStatisticsAggregate();
    }

}
