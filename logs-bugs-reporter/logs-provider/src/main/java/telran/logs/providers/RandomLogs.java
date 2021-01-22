package telran.logs.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.interfaces.IRandomLogs;

@Component
public class RandomLogs implements IRandomLogs {

    @Autowired
    RandomLogsRepo logs;

    static Map<LogType, String> artifacts = new EnumMap<>(LogType.class);
    {
	artifacts.put(LogType.AUTHENTIATION_EXCEPTION, "authentication");
	artifacts.put(LogType.ATHORIZATION_EXCEPTION, "authorization");
	LogType[] typesClass = { LogType.NO_EXCEPTION, LogType.BAD_REQUEST_EXCEPTION, LogType.NOT_FOUND_EXCEPTION,
		LogType.DUPLICATED_KEY_EXCEPTION };
	for (LogType type : typesClass) {
	    artifacts.put(type, String.format("class %d", getRandomInt(1, 100)));
	}
    }

    @Override
    public LogDto createRandomLog() {
	Date dateTime = new Date();
	LogType exception = generateLogType();
	String artifact = artifacts.get(exception);
	int responseTime = 0;
	if (exception == LogType.NO_EXCEPTION)
	    responseTime = getRandomInt(1, 100);
	return new LogDto(dateTime, exception, artifact, responseTime, "");

    }

    @Override
    public List<LogDto> generateLogs(int count) {
	logs.deleteAll();
	List<LogDto> docs = new ArrayList<LogDto>();
	List<LogDoc> newDocs = new ArrayList<LogDoc>();
	for (int i = 0; i < count; i++) {
	    LogDto dto = createRandomLog();
	    docs.add(dto);
	    newDocs.add(new LogDoc(dto));
	}

	logs.saveAll(newDocs);
	return docs;
    }

    @Override
    public void getStatisticsAggregate() {
	logs.getStatisticsAggregate().forEach(System.out::println);
	logs.getStatistics().forEach(System.out::println);
    }
    private LogType generateLogType() {
	int exception = getRandomInt(1, 100);
	if (exception <= 10) {
	    int security = getRandomInt(1, 100);
	    if (security <= 30) {
		int prob = getRandomInt(1, 100);
		if (prob <= 30) {
		    return LogType.AUTHENTIATION_EXCEPTION;
		}
		return LogType.ATHORIZATION_EXCEPTION;
	    }
	    int nonSecurity = getRandomInt(1, 100);
	    if (nonSecurity <= 25) {
		return LogType.BAD_REQUEST_EXCEPTION;
	    } else if (nonSecurity > 25 && nonSecurity <= 50) {
		return LogType.NOT_FOUND_EXCEPTION;
	    } else if (nonSecurity > 50 && nonSecurity <= 75) {
		return LogType.DUPLICATED_KEY_EXCEPTION;
	    }
	    return LogType.SERVER_EXCEPTION;
	}
	return LogType.NO_EXCEPTION;

    }

    private int getRandomInt(int min, int max) {
	return ThreadLocalRandom.current().nextInt(max - min) + min;
    }
}
