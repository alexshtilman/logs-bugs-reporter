package telran.logs.bugs.controllers;

import static telran.logs.bugs.api.Constants.ARTIFACT_AND_COUNT;
import static telran.logs.bugs.api.Constants.INTEGERS;
import static telran.logs.bugs.api.Constants.LOGTYPE_AND_COUNT;
import static telran.logs.bugs.api.Constants.MOST_ENCOUNTERED_ARTIFACTS;
import static telran.logs.bugs.api.Constants.MOST_ENCOUNTERED_EXCEPTIONS;
import static telran.logs.bugs.api.Constants.STATISTICS_CONTROLLER;
import static telran.logs.bugs.api.Constants.STRINGS_FLUX;
import static telran.logs.bugs.api.Constants.STRINGS_LIST;
import static telran.logs.bugs.api.Constants.STRINGS_MONO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import telran.logs.bugs.dto.ArtifactAndCountDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.LogTypeAndCountDto;
import telran.logs.bugs.interfaces.LogsInfo;

@RestController
@RequestMapping(STATISTICS_CONTROLLER)
public class StatisticsController {

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
	public Mono<List<String>> getFirstMostEncounteredArtifacts(
			@RequestParam(name = "count", defaultValue = "3") int count) {
		return logsInfo.getFirstMostEncounteredArtifacts(count).collectList();
	}

	@GetMapping(value = ARTIFACT_AND_COUNT)
	public Flux<ArtifactAndCountDto> getArtifactOccuresnces() {
		return logsInfo.getArtifactOccuresnces();
	}

	@GetMapping(value = STRINGS_FLUX)
	public Flux<String> getStrings() {
		return Flux.just("1", "2", "3", "4");
	}

	@GetMapping(value = STRINGS_LIST)
	public List<String> getIStringGList() {
		return List.of("1", "2", "3", "4");
	}

	@GetMapping(value = STRINGS_MONO)
	public Mono<List<String>> getIntegersList() {
		return Flux.just("1", "2", "3", "4").collectList();
	}

	@GetMapping(value = INTEGERS)
	public Flux<Integer> getIntegers() {
		return Flux.just(1, 2, 3, 4);
	}

}
