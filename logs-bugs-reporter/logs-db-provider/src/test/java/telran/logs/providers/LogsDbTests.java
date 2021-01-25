package telran.logs.providers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class LogsDbTests {

    @Autowired
    InputDestination input;

    @Autowired
    OutputDestination output;

    @Test
    void sendExceptionLogs() throws InterruptedException {

	assertDoesNotThrow(() -> {
	    input.send(new GenericMessage<LogDto>(new LogDto(new Date(), LogType.NO_EXCEPTION, "test", 10, "Correct")));
	});

	assertThrows(Exception.class, () -> input
		.send(new GenericMessage<LogDto>(new LogDto(null, LogType.NO_EXCEPTION, "test", 10, "FAIL1"))));

	assertThrows(Exception.class,
		() -> input.send(new GenericMessage<LogDto>(new LogDto(new Date(), null, "test", 10, "FAIL2"))));

	assertThrows(Exception.class,
		() -> input.send(
			new GenericMessage<LogDto>(new LogDto(new Date(), LogType.NO_EXCEPTION, "", 10, "FAIL3"))));

    }

    @Disabled
    @Test
    void getLogs() {
	byte[] messageBytes = output.receive().getPayload();
	String messageString = new String(messageBytes);
    }
}
