package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.jdbc.Sql;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.BugStatus;
import telran.logs.bugs.jpa.entities.OppeningMethod;
import telran.logs.bugs.jpa.entities.Seriosness;
import telran.logs.bugs.repositories.BugsRepo;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@AutoConfigureTestDatabase
@Log4j2
class OpenningLogsBugsTest {

	@Autowired
	BugsRepo bugsRepo;

	@Autowired
	InputDestination producer;

	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;

	private static final String FILL_TABELS_SQL = "fillTabels.sql";

	@Test
	@Sql(FILL_TABELS_SQL)
	void testNonExistingArtifact() {
		LogDto logDto = new LogDto(new Date(), LogType.SERVER_EXCEPTION, "OppeningLogsBugsTest.class", 0, "data 1");
		testNewBugExists(logDto, BugStatus.OPEND, Seriosness.CRITICAL, null);
	}

	@Test
	@Sql(FILL_TABELS_SQL)
	void testExistingArtifactBlocking() {
		LogDto logDto = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "LogsAnalyzer.class", 0, "data 2");
		testNewBugExists(logDto, BugStatus.ASSIGNED, Seriosness.BLOCKING, 4L);
	}

	@Test
	@Sql(FILL_TABELS_SQL)
	void testExistingArtifactCriticalAuthorization() {
		LogDto logDto = new LogDto(new Date(), LogType.AUTHORIZATION_EXCEPTION, "LogDto.class", 0, "data 3");
		testNewBugExists(logDto, BugStatus.ASSIGNED, Seriosness.CRITICAL, 2L);
	}

	@Test
	@Sql(FILL_TABELS_SQL)
	void testExistingArtifactCriticalServer() {
		LogDto logDto = new LogDto(new Date(), LogType.SERVER_EXCEPTION, "LogDto.class", 0, "data 4");
		testNewBugExists(logDto, BugStatus.ASSIGNED, Seriosness.CRITICAL, 2L);
	}

	@Test
	@Sql(FILL_TABELS_SQL)
	void testNullDate() {
		LogDto logDto = new LogDto(null, LogType.NOT_FOUND_EXCEPTION, "LogsAnalyzer.class", 0, "data 5");
		testNewBugExists(logDto, BugStatus.ASSIGNED, Seriosness.MINOR, 4L);
	}

	@Test
	@Sql(FILL_TABELS_SQL)
	void testNullDateLogTypeEmptyArtifact() {
		LogDto logDto = new LogDto(null, null, "", 0, "ALL WRONG DATA");
		testNewBugExists(logDto, BugStatus.OPEND, Seriosness.MINOR, null);
	}

	public void testNewBugExists(LogDto logDto, BugStatus bugstatus, Seriosness seriosness, Long id) {
		producer.send(new GenericMessage<LogDto>(logDto));
		assertEquals(1, bugsRepo.count());
		Bug bug = bugsRepo.findAll().get(0);
		assertNotNull(bug.getDateOppen());
		assertNotNull(bug.getDescription());
		log.debug(bug.getDescription());
		assertNull(bug.getDateClose());
		assertEquals(bugstatus, bug.getStatus());
		assertEquals(seriosness, bug.getSeriosness());
		assertEquals(OppeningMethod.AUTOMATIC, bug.getOppeningMethod());
		if (id == null) {
			assertNull(bug.getProgrammer());
		} else {
			assertEquals(id, bug.getProgrammer().getId());
		}

	}
}
