package telran.logs.bugs.impl;

import static telran.logs.bugs.api.Constants.ARTIFACT_AND_COUNT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class LogsInfoImpl implements LogsInfo {

	@Autowired
	LogRepository logs;

	@Autowired
	CacheManager cacheManager;

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
	@Cacheable(value = ARTIFACT_AND_COUNT)
	public Flux<ArtifactAndCountDto> getArtifactOccuresnces() {
		log.debug("NO CACHE USED!");
		// FIXME TO DO FIND HOW TO WORK WITH CHAHE!
		log.debug(cacheManager.getCache(ARTIFACT_AND_COUNT).get(ARTIFACT_AND_COUNT, ArtifactAndCountDto.class));

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
