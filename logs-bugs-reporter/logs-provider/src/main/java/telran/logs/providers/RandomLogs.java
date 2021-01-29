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

@Component
public class RandomLogs implements IRandomLogs {

	@Autowired
	RandomLogsRepo logs;

	static Map<LogType, String> artifacts = new EnumMap<>(LogType.class);
	{
		artifacts.put(LogType.AUTHENTICATION_EXCEPTION, "authentication");
		artifacts.put(LogType.AUTHORIZATION_EXCEPTION, "authorization");
		LogType[] typesClass = { LogType.NO_EXCEPTION, LogType.BAD_REQUEST_EXCEPTION, LogType.NOT_FOUND_EXCEPTION,
				LogType.DUPLICATED_KEY_EXCEPTION, LogType.SERVER_EXCEPTION };
		for (LogType type : typesClass) {
			artifacts.put(type, String.format("class %d", getChance()));
		}
	}
	int secExceptionProb = 30;
	int exceptionProb = 10;
	int authenticationProb = 70;

	@Override
	public LogDto createRandomLog() {
		Date dateTime = new Date();
		LogType exception = generateLogType();
		String artifact = artifacts.get(exception);
		int responseTime = 0;
		if (exception == LogType.NO_EXCEPTION)
			responseTime = getChance();
		return new LogDto(dateTime, exception, artifact, responseTime, "");

	}

	@Override
	public List<LogDto> generateLogs(int count) {
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
		//TODO Auto-generated method stub
		logs.getStatisticsAggregate().forEach(System.out::println);
		logs.getStatistics().forEach(System.out::println);
	}

	private LogType generateLogType() {
		int chance = getChance();

		return chance <= exceptionProb ? getExceptionLog() : LogType.NO_EXCEPTION;
	}

	private LogType getExceptionLog() {

		return getChance() <= secExceptionProb ? getSecurityExceptionLog() : getNonSecurityExceptionLog();
	}

	private LogType getNonSecurityExceptionLog() {
		LogType nonSecExceptions[] = { LogType.BAD_REQUEST_EXCEPTION, LogType.DUPLICATED_KEY_EXCEPTION,
				LogType.NOT_FOUND_EXCEPTION, LogType.SERVER_EXCEPTION };
		int ind = ThreadLocalRandom.current().nextInt(0, nonSecExceptions.length);
		return nonSecExceptions[ind];
	}

	private LogType getSecurityExceptionLog() {
		return getChance() <= authenticationProb ? LogType.AUTHENTICATION_EXCEPTION : LogType.AUTHORIZATION_EXCEPTION;
	}

	private int getChance() {
		return ThreadLocalRandom.current().nextInt(1, 101);
	}
}
