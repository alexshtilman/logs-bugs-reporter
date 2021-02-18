package telran.logs.bugs.interfaces;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactAndCountDto;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.LogTypeAndCountDto;

public interface LogsInfo {
	Flux<LogDto> getAllLogs();

	Flux<LogDto> getAllExceptions();

	Flux<LogDto> getLogsTypes(LogType logType);

	Flux<LogTypeAndCountDto> getLogTypeOccurences();

	Flux<ArtifactAndCountDto> getArtifactOccuresnces();

	Flux<LogType> getFirstMostEncounteredExceptions(int count);

	Flux<String> getFirstMostEncounteredArtifacts(int count);

}
