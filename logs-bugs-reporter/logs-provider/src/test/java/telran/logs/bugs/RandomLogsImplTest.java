package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.components.RandomLogsComponent;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@Log4j2
class RandomLogsImplTest {

	OutputDestination output;
	RandomLogsComponent myLogs;

	@Autowired
	public RandomLogsImplTest(OutputDestination output, RandomLogsComponent myLogs) {
		super();
		this.output = output;
		this.myLogs = myLogs;
	}

	@Test
	void sendRandomLogs() throws InterruptedException {

		Set<String> data = new HashSet<>();
		int countOfMessages = 10;
		for (int i = 0; i < countOfMessages; i++) {
			try {
				byte[] messageBytes = output.receive(Long.MAX_VALUE).getPayload();
				String messageString = new String(messageBytes);
				data.add(messageString);
				log.info("recived message: {}", messageString);
			} catch (Exception e) {
				log.warn("error on reciving message because: {}", e.getMessage());
				i--;
			}
		}
		assertEquals(countOfMessages, data.size());
	}
}
