package telran.logs.bugs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Programmer;

@Component
@Log4j2
public class OpeningController {

	@Autowired
	ArtifactRepo artifactRepo;

	@Autowired
	ProgrammersRepo programmersRepo;

	@Value("${n-programmers:3}")
	int nProgrammers;
	@Value("${n-artifacts:10}")
	int nArtifacts;
	@Value("${programmers}")
	String[] programmers;
	@Value("${artifacts}")
	String[] artifacts;

	@GetMapping("/fill")
	public void init() {
		log.debug("generating random programmers {}", nProgrammers);

		String[] randomProgrammersNames = generateRandomData(programmers, nProgrammers, true);
		String[] randomArtifactNames = generateRandomData(artifacts, nArtifacts, false);

		List<Programmer> programmersList = new ArrayList<>();
		List<Artifact> artifactsList = new ArrayList<>();

		long i = 1;
		for (String programmer : randomProgrammersNames) {
			programmersList.add(new Programmer(i++, programmer));
		}
		programmersRepo.saveAll(programmersList);
		log.debug("generating random artifacts {}", nArtifacts);
		for (String artifact : randomArtifactNames) {
			artifactsList.add(new Artifact(artifact, programmersList.get(getRandomInt(0, nProgrammers))));
		}
		artifactRepo.saveAll(artifactsList);
	}

	public int getRandomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(max - min) + min;
	}

	public String[] generateRandomData(String[] data, int limit, boolean unique) {

		return unique
				? Stream.generate(() -> data[getRandomInt(0, data.length)]).distinct().limit(limit)
						.toArray(String[]::new)
				: Stream.generate(() -> data[getRandomInt(0, data.length)]).limit(limit).toArray(String[]::new);
	}
}
