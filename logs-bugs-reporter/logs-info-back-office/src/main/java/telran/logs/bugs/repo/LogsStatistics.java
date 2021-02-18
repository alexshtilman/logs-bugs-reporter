package telran.logs.bugs.repo;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactAndCountDto;
import telran.logs.bugs.dto.LogDocClass;
import telran.logs.bugs.dto.LogTypeAndCountDto;
import telran.logs.bugs.dto.LogTypeClass;

public interface LogsStatistics {

	Flux<LogTypeAndCountDto> getLogTypeOccurences();

	Flux<ArtifactAndCountDto> getArtifactOccuresnces();

	Flux<LogTypeClass> getFirstMostEncounteredExceptions(int count);

	Flux<LogDocClass> getFirstMostEncounteredArtifacts(int count);

}
