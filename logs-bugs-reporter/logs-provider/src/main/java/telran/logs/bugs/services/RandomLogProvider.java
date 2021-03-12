/**
 * 
 */
package telran.logs.bugs.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.repo.ArtifactRepo;
import telran.logs.bugs.jpa.repo.BugRepo;
import telran.logs.bugs.jpa.repo.ProgrammerRepo;

/**
 * @author Alex Shtilman Mar 12, 2021
 *
 */
@Service
@Log4j2
public class RandomLogProvider {
	@Value("${account-name:java36.logs+}")
	String gmailAccount;
	@Value("${popultae-db:false}")
	boolean populateEnabled;
	@Value("${random-names}")
	String[] randomNames;
	@Value("${random-artifacts}")
	String[] randomArtifacts;
	BugRepo bugsRepo;
	ArtifactRepo artifactRepo;
	ProgrammerRepo programmerRepo;

	@Autowired
	public RandomLogProvider(BugRepo bugsRepo, ArtifactRepo artifactRepo, ProgrammerRepo programmerRepo) {
		this.bugsRepo = bugsRepo;
		this.artifactRepo = artifactRepo;
		this.programmerRepo = programmerRepo;
	}

	@PostConstruct
	public void initDb() {
		log.debug("BubgsReporter is dropping database!");
		if (populateEnabled) {
			artifactRepo.deleteAll();
			bugsRepo.deleteAll();

			programmerRepo.deleteAll();

			List<Programmer> programmers = new ArrayList<>();
			List<Artifact> artifacts = new ArrayList<>();

			for (int i = 1; i < randomNames.length + 1; i++) {
				programmers.add(
						new Programmer(i, randomNames[i - 1], gmailAccount + randomNames[i - 1] + i + "@gmail.com"));
			}

			programmerRepo.saveAll(programmers);

			for (int i = 0; i < randomArtifacts.length; i++) {
				artifacts.add(new Artifact(randomArtifacts[i], programmers.get(getRandomInt(0, programmers.size()))));
			}

			artifactRepo.saveAll(artifacts);
			log.debug("BubgsReporter has created {} progrrammers and {} artifacts!", randomNames.length,
					randomArtifacts.length);
		}
	}

	public int getRandomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(max - min) + min;
	}

}
