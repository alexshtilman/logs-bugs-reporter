package telran.logs.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;

@Component
public class RandomLogs implements IRandomLogs {

	@Autowired
	RandomLogsRepo logs;

	@Autowired
	StreamBridge streamBridge;

	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;

	static Logger LOG = LoggerFactory.getLogger(RandomLogs.class);

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
	@Value("${sec-exception-prob:30}")
	int secExceptionProb;

	@Value("${exception-prob:10}")
	int exceptionProb;

	@Value("${auth=exception-prob:70}")
	int authenticationProb;

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
		List<LogDto> docs = new ArrayList<>();
		List<LogDoc> newDocs = new ArrayList<>();
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
		LOG.info("Performing aggregation by annotation");
		logs.getStatisticsAggregate().forEach(log -> LOG.info("{}", log));
		LOG.info("Performing aggregation by custom repor");
		logs.getStatistics().forEach(log -> LOG.info("{}", log));
	}

	private LogType generateLogType() {
		int chance = getChance();

		return chance <= exceptionProb ? getExceptionLog() : LogType.NO_EXCEPTION;
	}

	private LogType getExceptionLog() {

		return getChance() <= secExceptionProb ? getSecurityExceptionLog() : getNonSecurityExceptionLog();
	}

	private LogType getNonSecurityExceptionLog() {
		LogType[] nonSecExceptions = { LogType.BAD_REQUEST_EXCEPTION, LogType.DUPLICATED_KEY_EXCEPTION,
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
