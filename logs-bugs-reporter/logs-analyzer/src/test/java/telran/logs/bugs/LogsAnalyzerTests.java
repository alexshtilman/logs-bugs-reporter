package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class LogsAnalyzerTests {
	@Autowired
	InputDestination producer;

	@Autowired
	OutputDestination consumer;

	@Value("${app-binding-name}")
	String bindingName;

	static Logger LOG = LoggerFactory.getLogger(LogsAnalyzerTests.class);

	@Test
	void analyzerTestNonException() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "");
		producer.send(new GenericMessage<LogDto>(logDto));
		assertThrows(Exception.class, consumer::receive);
	}

	@BeforeEach
	void setup() {
		consumer.clear();
	}

	@Test
	void analyzerTestException() {
		LogDto logDto = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "");
		producer.send(new GenericMessage<LogDto>(logDto));
		// assertNull(consumer.receive(1000, bindingName + "42"));
		Message<byte[]> message = consumer.receive(0, bindingName);
		assertNotNull(message);
		LOG.debug("recived in consumer {}", new String(message.getPayload()));

	}
}
