package telran.aop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * 
 */

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

/**
 * @author Alex Shtilman Apr 14, 2021
 *
 */
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:componentTest.properties")
@Import({ TestChannelBinderConfiguration.class, LoggingComponentConfig.class })
@EnableAutoConfiguration
@Log4j2
class LoggingComponentTest {

	@Value("${app-binding-name}")
	String bindingName;

	@Autowired
	LoggingComponent logger;

	@Autowired
	OutputDestination consumer;

	@Test
	void sendLogs() {

		int countOfMessages = 10;
		LogDto dto = LogDto.builder().artifact("test").dateTime(new Date()).logType(LogType.NO_EXCEPTION)
				.responseTime(0).result("200").build();
		for (int i = 0; i < countOfMessages; i++) {
			logger.sendLog(dto);
		}
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < countOfMessages; i++) {
			Message<byte[]> message = consumer.receive(Long.MAX_VALUE, bindingName);
			assertNotNull(message);
			String json = new String(message.getPayload());
			result.add(json);
			log.debug("Recived from streamBrige {}, message {}", bindingName, json);
		}
		Message<byte[]> message = consumer.receive(0, bindingName);
		assertNull(message);
		assertEquals(countOfMessages, result.size());

	}
}
