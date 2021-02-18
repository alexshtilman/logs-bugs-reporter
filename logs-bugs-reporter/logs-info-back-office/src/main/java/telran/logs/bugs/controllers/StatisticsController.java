package telran.logs.bugs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactAndCountDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.LogTypeAndCountDto;
import telran.logs.bugs.interfaces.LogsInfo;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
	private static final String ARTIFACT_AND_COUNT = "/artifact_and_count";
	private static final String MOST_ENCOUNTERED_ARTIFACTS = "/most_encountered_artifacts";
	private static final String MOST_ENCOUNTERED_EXCEPTIONS = "/most_encountered_exceptions";
	private static final String LOGTYPE_AND_COUNT = "/logtype_and_count";

	@Autowired
	LogsInfo logsInfo;

	@GetMapping(value = LOGTYPE_AND_COUNT)
	public Flux<LogTypeAndCountDto> getLogTypeOccurences() {
		return logsInfo.getLogTypeOccurences();
	}

	@GetMapping(value = MOST_ENCOUNTERED_EXCEPTIONS)
	public Flux<LogType> getFirstMostEncounteredExceptions(
			@RequestParam(name = "count", defaultValue = "3") int count) {
		return logsInfo.getFirstMostEncounteredExceptions(count);

	}

	@GetMapping(value = MOST_ENCOUNTERED_ARTIFACTS)
	public Flux<String> getFirstMostEncounteredArtifacts(@RequestParam(name = "count", defaultValue = "3") int count) {
		return logsInfo.getFirstMostEncounteredArtifacts(count);
	}

	@GetMapping(value = ARTIFACT_AND_COUNT)
	public Flux<ArtifactAndCountDto> getArtifactOccuresnces() {
		return logsInfo.getArtifactOccuresnces();
	}

}
