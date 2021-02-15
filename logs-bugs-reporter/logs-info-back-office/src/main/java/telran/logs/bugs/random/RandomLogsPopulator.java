package telran.logs.bugs.random;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.components.RandomLogsComponent;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.repo.LogRepository;

@Component
@Log4j2
public class RandomLogsPopulator {

	@Autowired
	RandomLogsComponent randomlogs;

	@Value("${app-number-logs:0}")
	int nLogs;
	@Value("${app-population-enable:false}")
	boolean flPopulation;
	@Autowired
	LogRepository logRepo;

	@PostConstruct
	void populateDB() {
		if (flPopulation) {
			log.debug("Starting db population");
			List<LogDoc> logs = getRandomLogs();
			logRepo.saveAll(logs).buffer().blockFirst();
			log.debug("Saved {} logs", logs.size());
		}
	}

	private List<LogDoc> getRandomLogs() {
		return Stream.generate(() -> new LogDoc(randomlogs.createRandomLog())).parallel().limit(nLogs)
				.collect(Collectors.toList());
	}
}
