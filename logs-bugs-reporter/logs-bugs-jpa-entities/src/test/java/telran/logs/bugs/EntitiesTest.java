package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.BugStatus;
import telran.logs.bugs.jpa.entities.OppeningMethod;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.entities.Seriosness;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes = { ArtifactRepo.class, BugsRepo.class, ProgrammersRepo.class })
public class EntitiesTest {

	@Autowired
	ArtifactRepo artifactRepo;

	@Autowired
	BugsRepo bugsRepo;

	@Autowired
	ProgrammersRepo programmersRepo;

	@Test
	void inital() {
		Programmer programmer = new Programmer(123, "Moshe");
		Artifact artifact = new Artifact("authentication", programmer);
		programmersRepo.save(programmer);
		artifactRepo.save(artifact);
		Bug bug = new Bug("descri", LocalDate.now(), null, BugStatus.ASSIGNED, Seriosness.MINOR,
				OppeningMethod.AUTOMATICK, programmer);
		bugsRepo.save(bug);
		List<Bug> bugs = bugsRepo.findAll();
		assertEquals(1, bugs.size());
		assertEquals(bug, bugs.get(0));
	}
}
