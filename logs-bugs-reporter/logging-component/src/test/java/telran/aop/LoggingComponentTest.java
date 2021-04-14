package telran.aop;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * 
 */

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

/**
 * @author Alex Shtilman Apr 14, 2021
 *
 */
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:componentTest.properties")
@EnableAutoConfiguration
class LoggingComponentTest {

	@Autowired
	LoggingComponent logger;

	@Test
	void sendRandomLogs() throws InterruptedException {

		Set<String> data = new HashSet<>();
		int countOfMessages = 10;
		LogDto dto = LogDto.builder().artifact("test").dateTime(new Date()).logType(LogType.NO_EXCEPTION)
				.responseTime(0).result("200").build();
		for (int i = 0; i < countOfMessages; i++) {
			logger.sendLog(dto);
		}

//		for (int i = 0; i < countOfMessages; i++) {
//			byte[] messageBytes = output.receive(Long.MAX_VALUE).getPayload();
//			String messageString = new String(messageBytes);
//			data.add(messageString);
//		}
//		assertEquals(countOfMessages, data.size());
	}
}
