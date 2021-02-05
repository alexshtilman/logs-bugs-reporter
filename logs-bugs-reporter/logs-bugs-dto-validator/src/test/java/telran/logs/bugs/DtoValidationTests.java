package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@Log4j2
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class DtoValidationTests {

	@Autowired
	InputDestination input;

	@Autowired
	OutputDestination consumer;

	@Value("${app-binding-name:logs-all-out-0}")
	String allLogs;

	@Value("${app-binding-name-exceptions:logs-only-exception-out-0}")
	String onlyExceptions;

	List<LogDto> logDtoList = new ArrayList<>();
	{
		logDtoList.add(new LogDto(new Date(), LogType.NO_EXCEPTION, "valid", 10, ""));

		logDtoList.add(new LogDto(null, LogType.NO_EXCEPTION, "null date", 10, ""));
		logDtoList.add(new LogDto(new Date(), null, "null LogType", 10, ""));
		logDtoList.add(new LogDto(new Date(), LogType.NO_EXCEPTION, "", 10, ""));
		logDtoList.add(new LogDto(null, null, "null date & null LogType", 10, ""));
		logDtoList.add(new LogDto(null, LogType.NO_EXCEPTION, "", 10, ""));
		logDtoList.add(new LogDto(new Date(), null, "", 10, ""));
		logDtoList.add(new LogDto(null, null, "", 10, ""));
	}

	@Test
	void testAllLogs() {
		tesCount(8, allLogs);
	}

	@Test
	void testOnlyExceptions() {
		tesCount(7, onlyExceptions);
	}

	@SneakyThrows
	public void tesCount(int expectedCount, String bindingName) {
		logDtoList.forEach(logDto -> input.send(new GenericMessage<LogDto>(logDto)));
		for (int i = 0; i < expectedCount; i++) {
			Message<byte[]> message = consumer.receive(Long.MAX_VALUE, bindingName);
			assertNotNull(message);
			log.debug("{} Recived from streamBrige {}, message {}", i, bindingName, new String(message.getPayload()));
		}

		Message<byte[]> message = consumer.receive(1000, bindingName);
		assertNull(message);

	}
}
