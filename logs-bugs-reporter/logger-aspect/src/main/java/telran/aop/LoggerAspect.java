/**
 * 
 */
package telran.aop;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.exceptions.DuplicatedException;
import telran.logs.bugs.exceptions.NotFoundException;

/**
 * @author Alex Shtilman Apr 13, 2021
 *
 */
@Component
@Aspect
public class LoggerAspect {

	@Value("${app-responce-result:200}")
	String defaultResult;

	@Autowired
	LoggingComponent loggingComponent;

	@Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
	void pointCutRestController() {
	}

	@Around("pointCutRestController()")
	Object loggingAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		Instant start = Instant.now();
		Date currentDate = new Date();
		String className = joinPoint.getTarget().getClass().getSimpleName();
		Object res = null;
		LogDto dto = LogDto.builder().dateTime(currentDate).logType(LogType.NO_EXCEPTION).responseTime(0)
				.artifact(className).build();
		try {
			res = joinPoint.proceed();
		} catch (IllegalArgumentException e) {
			dto.logType = LogType.BAD_REQUEST_EXCEPTION;
			dto.result = e.getMessage();
			loggingComponent.sendLog(dto);
			throw e;
		} catch (DuplicatedException e) {
			dto.logType = LogType.DUPLICATED_KEY_EXCEPTION;
			dto.result = e.getMessage();
			loggingComponent.sendLog(dto);
			throw e;
		} catch (NotFoundException e) {
			dto.logType = LogType.NOT_FOUND_EXCEPTION;
			dto.result = e.getMessage();
			loggingComponent.sendLog(dto);
			throw e;
		} catch (Throwable e) {
			dto.logType = LogType.SERVER_EXCEPTION;
			dto.result = e.getMessage();
			loggingComponent.sendLog(dto);
			throw e;
		}
		dto.responseTime = (int) ChronoUnit.MILLIS.between(start, Instant.now());
		dto.result = res == null ? "void" : defaultResult;
		loggingComponent.sendLog(dto);

		return res;
	}
}
