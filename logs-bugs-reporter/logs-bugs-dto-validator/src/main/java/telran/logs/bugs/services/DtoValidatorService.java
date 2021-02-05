package telran.logs.bugs.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@Service
@Log4j2
public class DtoValidatorService implements IDtoValidatorService {
	@Autowired
	StreamBridge streamBridge;

	@Value("${app-binding-name:logs-all-out-0}")
	String allLogs;

	@Value("${app-binding-name-exceptions:logs-only-exception-out-0}")
	String onlyExceptions;

	@Autowired
	Validator validator;

	@Override
	public void validateDto(LogDto logDto) {
		log.debug("Recived dto: {}", logDto);
		Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);
		List<String> errors = new ArrayList<>();
		if (!violations.isEmpty()) {
			violations.forEach(cv -> errors.add("{" + cv.getPropertyPath() + ":'" + cv.getMessage() + "'}"));
			logDto = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, DtoValidatorService.class.toString(), 0,
					errors.toString());
			log.debug("Found validation errors: {}", errors.toString());
			streamBridge.send(onlyExceptions, logDto);
			log.debug("Has sent data to {}", onlyExceptions);
		} else
			log.debug("No validation errors");

		streamBridge.send(allLogs, logDto);
		log.debug("Has sent to {} logDto: {}", allLogs, logDto);
	}
}
