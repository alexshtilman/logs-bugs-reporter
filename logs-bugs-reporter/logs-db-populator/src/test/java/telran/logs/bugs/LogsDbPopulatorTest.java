package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.springframework.messaging.support.GenericMessage;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.mongo.repo.LogsRepo;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class LogsDbPopulatorTest {

	@Autowired
	InputDestination input;

	@Autowired
	LogsRepo consumerLogs;

	@Autowired
	OutputDestination consumer;

	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;

	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorTest.class);

	@BeforeEach
	void setup() {
		consumerLogs.deleteAll();
	}

	@Test
	void sendNullDate() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "test", 10, "");
		logDto.dateTime = null;
		input.send(new GenericMessage<LogDto>(logDto));
		testBadRequestFields();
	}

	@Test
	void sendNullLogType() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "test", 10, "");
		logDto.logType = null;
		input.send(new GenericMessage<LogDto>(logDto));
		testBadRequestFields();
	}

	@Test
	void sendEmptyArtifact() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "test", 10, "");
		logDto.artifact = "";
		input.send(new GenericMessage<LogDto>(logDto));
		testBadRequestFields();
	}

	@Test
	void sendNullMany() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "test", 10, "");
		logDto.dateTime = null;
		logDto.logType = null;
		logDto.artifact = "";
		input.send(new GenericMessage<LogDto>(logDto));
		testBadRequestFields();
	}

	public void testBadRequestFields() {
		assertEquals(1, consumerLogs.count());
		LogDoc doc = consumerLogs.findAll().get(0);
		LOG.debug("Recived LogDto: {}}", doc);
	}

	@Test
	void sendNormalDto() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "test", 10, "");
		input.send(new GenericMessage<LogDto>(logDto));
		assertEquals(1, consumerLogs.count());
		LogDoc doc = consumerLogs.findAll().get(0);
		assertEquals(LogType.NO_EXCEPTION, doc.getLogDto().getLogType());
	}
}
