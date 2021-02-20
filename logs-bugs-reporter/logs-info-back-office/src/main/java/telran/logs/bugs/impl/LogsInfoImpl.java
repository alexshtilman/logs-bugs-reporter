package telran.logs.bugs.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactAndCountDto;
import telran.logs.bugs.dto.LogDocClass;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.LogTypeAndCountDto;
import telran.logs.bugs.interfaces.LogsInfo;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.repo.LogRepository;

@Service
public class LogsInfoImpl implements LogsInfo {

	@Autowired
	LogRepository logs;

	@Override
	public Flux<LogDto> getAllLogs() {
		return logs.findAll().map(LogDoc::getLogDto);
	}

	@Override
	public Flux<LogDto> getAllExceptions() {
		return logs.findByLogTypeNot(LogType.NO_EXCEPTION).map(LogDoc::getLogDto);
	}

	@Override
	public Flux<LogDto> getLogsTypes(LogType logType) {
		return logs.findByLogType(logType).map(LogDoc::getLogDto);
	}

	@Override
	public Flux<LogTypeAndCountDto> getLogTypeOccurences() {
		return logs.getLogTypeOccurencesByAggregation();
	}

	@Override
	public Flux<ArtifactAndCountDto> getArtifactOccuresnces() {
		return logs.getArtifactOccuresncesByAggregation();

	}

	@Override
	public Flux<LogType> getFirstMostEncounteredExceptions(int count) {
		return logs.getFirstMostEncounteredExceptionsByAggregation(count).map(x -> x.logType);
	}

	@Override
	public Flux<String> getFirstMostEncounteredArtifacts(int count) {
		return logs.getFirstMostEncounteredArtifactsByAggregation(count).map(LogDocClass::getArtifact);
	}
}
