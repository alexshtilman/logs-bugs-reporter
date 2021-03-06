package telran.logs.bugs.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@Service
@Log4j2
public class LogsAnalyzerService {
	@Autowired
	StreamBridge streamBridge;

	@Value("${app-binding-name:logs-out-0}")
	String bindingNameLogs;

	@Value("${app-binding-name-exceptions:exceptions-out-0}")
	String bindingNameExceptions;

	@Autowired
	Validator validator;

	@Bean
	Consumer<LogDto> getAnalyzerBean() {
		return this::analyzeDto;
	}

	void analyzeDto(LogDto logDto) {
		log.debug("Recived dto: {}", logDto);
		Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);
		List<String> errors = new ArrayList<>();
		if (!violations.isEmpty()) {
			violations.forEach(cv -> errors.add("{" + cv.getPropertyPath() + ":'" + cv.getMessage() + "'}"));
			logDto = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, LogsAnalyzerService.class.toString(), 0,
					errors.toString());
			log.debug("Found validation errors: {}", errors.toString());
		}
		if (logDto.logType != LogType.NO_EXCEPTION) {
			streamBridge.send(bindingNameExceptions, logDto);
			log.debug("Has sent data to {}", bindingNameExceptions);
		}
		streamBridge.send(bindingNameLogs, logDto);
		log.debug("Has sent to {} logDto: {}", bindingNameLogs, logDto);
	}
}
