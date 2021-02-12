package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import reactor.test.StepVerifier;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
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

	@BeforeEach
	void setup() {
		consumerLogs.deleteAll().log().subscribe();
	}

	@Test
	void sendNormalDto() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "test", 10, "");
		sendAndAssertExpected(logDto);
	}

	@Test
	void sendNullDate() {
		LogDto logDto = new LogDto(null, LogType.NO_EXCEPTION, "test", 10, "");
		sendAndAssertExpected(logDto);
	}

	@Test
	void sendNullLogType() {
		LogDto logDto = new LogDto(new Date(), null, "test", 10, "");
		sendAndAssertExpected(logDto);
	}

	@Test
	void sendEmptyArtifact() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "", 10, "");
		sendAndAssertExpected(logDto);
	}

	@Test
	void sendNullMany() {
		LogDto logDto = new LogDto(null, null, "", 10, "");
		sendAndAssertExpected(logDto);
	}

	public void sendAndAssertExpected(LogDto dto) {
		input.send(new GenericMessage<LogDto>(dto));
		StepVerifier.create(consumerLogs.count()).expectNextCount(1).verifyComplete();
		StepVerifier.create(consumerLogs.findAll()).assertNext(doc -> {
			assertEquals(dto.dateTime, doc.getLogDto().dateTime);
			assertEquals(dto.logType, doc.getLogDto().logType);
			assertEquals(dto.artifact, doc.getLogDto().artifact);
			assertEquals(dto.responseTime, doc.getLogDto().responseTime);
			assertEquals(dto.result, doc.getLogDto().result);
		}).expectNextCount(0).verifyComplete();
	}
}
