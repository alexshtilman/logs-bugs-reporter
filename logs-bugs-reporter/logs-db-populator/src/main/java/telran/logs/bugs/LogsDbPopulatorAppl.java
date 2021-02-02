package telran.logs.bugs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;

@SpringBootApplication
@Log4j2
public class LogsDbPopulatorAppl {

	@Autowired
	LogsDbRepo consumerLogs;

	@Autowired
	StreamBridge streamBridge;

	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;

	public static void main(String[] args) {

		SpringApplication.run(LogsDbPopulatorAppl.class, args);

	}

	@Bean
	Consumer<LogDto> getLogDtoCounsumer() {
		return this::takeLogDto;
	}

	@Autowired
	Validator validator;

	public void takeLogDto(LogDto logDto) {
		log.debug("recived log {}", logDto);

		Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);

		if (!violations.isEmpty()) {
			List<ApiError> erorrs = new ArrayList<>();

			violations.forEach(cv -> erorrs.add(new ApiError(cv.getPropertyPath().toString(), cv.getMessage(),
					cv.getInvalidValue() == null ? "null" : cv.getInvalidValue().toString())));
			logDto = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, LogsDbPopulatorAppl.class.toString(), 0,
					erorrs.toString());
			consumerLogs.save(new LogDoc(logDto));
			log.debug("saved with exception because: {}", erorrs);
			streamBridge.send(bindingName, logDto);
		} else {

			consumerLogs.save(new LogDoc(logDto));
			log.info("saved new log {}", logDto);

		}

	}
}
