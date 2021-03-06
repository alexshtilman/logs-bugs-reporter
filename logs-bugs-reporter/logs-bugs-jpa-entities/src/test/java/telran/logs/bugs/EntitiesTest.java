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

import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.OpenningMethod;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.Programmer;

/**
 * 
 * @author Alex Shtilman Feb 22, 2021
 *
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes = { ArtifactRepo.class, BugsRepo.class, ProgrammersRepo.class })
class EntitiesTest {

	ArtifactRepo artifactRepo;
	BugsRepo bugsRepo;
	ProgrammersRepo programmersRepo;

	@Autowired
	public EntitiesTest(ArtifactRepo artifactRepo, BugsRepo bugsRepo, ProgrammersRepo programmersRepo) {
		super();
		this.artifactRepo = artifactRepo;
		this.bugsRepo = bugsRepo;
		this.programmersRepo = programmersRepo;
	}

	@Test
	void inital() {
		Programmer programmer = new Programmer(123, "Moshe", "moshe@gmail.com");
		Artifact artifact = new Artifact("authentication", programmer);
		programmersRepo.save(programmer);
		artifactRepo.save(artifact);
		Bug bug = new Bug("description", LocalDate.now(), null, BugStatus.ASSIGNED, Seriousness.MINOR,
				OpenningMethod.AUTOMATIC, programmer);
		bugsRepo.save(bug);
		List<Bug> bugs = bugsRepo.findAll();
		assertEquals(1, bugs.size());
		assertEquals(bug, bugs.get(0));
	}
}
