package telran.logs.bugs.components;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

/**
 * Component used to generate random {@link telran.logs.bugs.dto.LogDto}
 * 
 * @since homework 64
 *
 */
@Component
@EnableConfigurationProperties
@PropertySource("classpath:application.properties")
public class RandomLogsComponent {

	@Value("${sec-exception-prob:30}")
	int secExceptionProb;

	@Value("${exception-prob:10}")
	int exceptionProb;

	@Value("${auth-exception-prob:70}")
	int authenticationProb;

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

	public LogDto createRandomLog() {
		Date dateTime = new Date();
		LogType exception = generateLogType();
		String artifact = artifacts.get(exception);
		int responseTime = 0;
		if (exception == LogType.NO_EXCEPTION)
			responseTime = getChance();
		return new LogDto(dateTime, exception, artifact, responseTime, "");

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
