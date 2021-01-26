package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class LogsDbPopulatorTest {


    @Autowired
    InputDestination input;

    @Autowired
    LogsDbRepo consumerLogs;

    @BeforeEach
    void setup() {
	consumerLogs.deleteAll();
    }

    @Test
    void sendNullDate() {
	LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "test", 10, "");
	logDto.dateTime = null;
	input.send(new GenericMessage<LogDto>(logDto));
	assertEquals(0, consumerLogs.count());
    }

    @Test
    void sendNullLogType() {
	LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "test", 10, "");
	logDto.logType = null;
	input.send(new GenericMessage<LogDto>(logDto));
	assertEquals(0, consumerLogs.count());
    }
    @Test
    void sendEmptyArtifact() {
	LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "test", 10, "");
	logDto.artifact = "";
	input.send(new GenericMessage<LogDto>(logDto));
	assertEquals(0, consumerLogs.count());
    }

    @Test
    void sendNormalDto() {
	LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "test", 10, "");
	input.send(new GenericMessage<LogDto>(logDto));
	assertEquals(1, consumerLogs.count());
    }
}
