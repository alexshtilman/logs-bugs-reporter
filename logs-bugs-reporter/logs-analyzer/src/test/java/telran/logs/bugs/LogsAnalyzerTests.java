package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

/**
 * 
 * @author Alex Shtilman Feb 22, 2021
 * 
 */
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@Log4j2
class LogsAnalyzerTests {

	@Autowired
	InputDestination input;

	@Autowired
	OutputDestination consumer;

	@Value("${app-binding-name:validated-out-0}")
	String validated;

	@Value("${app-binding-name-exceptions:exception-out-0}")
	String exceptions;

	List<LogDto> logDtoList = new ArrayList<>();
	{
		logDtoList.add(new LogDto(new Date(), LogType.NO_EXCEPTION, "valid", 10, "valid 1"));

		logDtoList.add(new LogDto(null, LogType.NO_EXCEPTION, "null date", 10, "invalid 2"));
		logDtoList.add(new LogDto(new Date(), null, "null LogType", 10, "invalid 3"));
		logDtoList.add(new LogDto(new Date(), LogType.NO_EXCEPTION, "", 10, "invalid 4"));
		logDtoList.add(new LogDto(null, null, "null date & null LogType", 10, "invalid 5"));
		logDtoList.add(new LogDto(null, LogType.NO_EXCEPTION, "", 10, "invalid 6"));
		logDtoList.add(new LogDto(new Date(), null, "", 10, "invalid 7"));
		logDtoList.add(new LogDto(null, null, "", 10, "invalid 8"));
	}

	@Test
	void testLogs() {
		logDtoList.forEach(logDto -> input.send(new GenericMessage<LogDto>(logDto)));
		List<String> resultAllLogs = receiveFromLogs(8, validated);
		assertTrue(resultAllLogs.get(0).contains("NO_EXCEPTION"));
		List<String> resultOnlyExceptions = receiveFromLogs(7, exceptions);
		for (String res : resultOnlyExceptions) {
			assertTrue(res.contains("BAD_REQUEST_EXCEPTION"));
		}
	}

	public List<String> receiveFromLogs(int expectedCount, String bindingName) {
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < expectedCount; i++) {
			Message<byte[]> message = consumer.receive(Long.MAX_VALUE, bindingName);
			assertNotNull(message);
			String json = new String(message.getPayload());
			result.add(json);
			log.debug("Recived from streamBrige {}, message {}", bindingName, json);
		}
		// there should no more messages
		Message<byte[]> message = consumer.receive(0, bindingName);
		assertNull(message);
		return result;
	}
}
