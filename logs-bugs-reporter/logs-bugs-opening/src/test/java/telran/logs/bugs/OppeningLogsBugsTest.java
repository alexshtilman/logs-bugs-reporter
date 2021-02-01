package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.jdbc.Sql;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.BugStatus;
import telran.logs.bugs.jpa.entities.OppeningMethod;
import telran.logs.bugs.jpa.entities.Seriosness;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@AutoConfigureTestDatabase
@Log4j2
class OppeningLogsBugsTest {

	private static final String NO_PROGRAMMER = "NO PROGRAMMER";

	@Autowired
	BugsRepo bugsRepo;

	@Autowired
	InputDestination producer;

	@Autowired
	OutputDestination consumer;

	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;

	private static final String FILL_TABELS_SQL = "fillTabels.sql";

	@BeforeEach
	void setup() {
		consumer.clear();
	}

	@Test
	@Sql(FILL_TABELS_SQL)
	void testNonExistingArtifact() {
		LogDto logDto = new LogDto(new Date(), LogType.SERVER_EXCEPTION, "OppeningLogsBugsTest.class", 0,
				NO_PROGRAMMER);
		testNewBugExists(logDto, LogType.SERVER_EXCEPTION + ", " + NO_PROGRAMMER, BugStatus.OPEND, Seriosness.CRITICAL,
				-1);
		assertThrows(Exception.class, consumer::receive);
	}

	public void testNewBugExists(LogDto logDto, String description, BugStatus bugstatus, Seriosness seriosness,
			int id) {
		producer.send(new GenericMessage<LogDto>(logDto));
		assertEquals(1, bugsRepo.count());
		Bug bug = bugsRepo.findAll().get(0);
		assertEquals(description, bug.getDescription());
		assertNotNull(bug.getDateOppen());
		assertNull(bug.getDateClose());
		assertEquals(bugstatus, bug.getStatus());
		assertEquals(seriosness, bug.getSeriosness());
		assertEquals(OppeningMethod.AUTOMATIC, bug.getOppeningMethod());
		assertEquals(id, bug.getProgrammer().getId());
	}

	@Test
	@Sql(FILL_TABELS_SQL)
	void testExistingArtifactBlocking() {
		LogDto logDto = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "LogsAnalyzer.class", 0,
				NO_PROGRAMMER);
		testNewBugExists(logDto, LogType.AUTHENTICATION_EXCEPTION + ", " + NO_PROGRAMMER, BugStatus.ASSIGNED,
				Seriosness.BLOCKING, 4);
		assertThrows(Exception.class, consumer::receive);
	}

	@Test
	@Sql(FILL_TABELS_SQL)
	void testExistingArtifactCriticalAuthorization() {
		LogDto logDto = new LogDto(new Date(), LogType.AUTHORIZATION_EXCEPTION, "LogDto.class", 0, NO_PROGRAMMER);
		testNewBugExists(logDto, LogType.AUTHORIZATION_EXCEPTION + ", " + NO_PROGRAMMER, BugStatus.ASSIGNED,
				Seriosness.CRITICAL, 2);
		assertThrows(Exception.class, consumer::receive);
	}

	@Test
	@Sql(FILL_TABELS_SQL)
	void testExistingArtifactCriticalServer() {
		LogDto logDto = new LogDto(new Date(), LogType.SERVER_EXCEPTION, "LogDto.class", 0, NO_PROGRAMMER);
		testNewBugExists(logDto, LogType.SERVER_EXCEPTION + ", " + NO_PROGRAMMER, BugStatus.ASSIGNED,
				Seriosness.CRITICAL, 2);
		assertThrows(Exception.class, consumer::receive);
	}

	@Test
	@Sql(FILL_TABELS_SQL)
	void testNullDate() {
		LogDto logDto = new LogDto(null, LogType.NOT_FOUND_EXCEPTION, "LogsAnalyzer.class", 0, NO_PROGRAMMER);
		testNewBugExists(logDto, LogType.BAD_REQUEST_EXCEPTION + ", [{\"dateTime\": \"must not be null\"}]",
				BugStatus.OPEND, Seriosness.MINOR, -1);

		Message<byte[]> message = consumer.receive(Long.MAX_VALUE, bindingName);
		assertNotNull(message);
		log.debug("recived in consumer {}", new String(message.getPayload()));
	}
}
