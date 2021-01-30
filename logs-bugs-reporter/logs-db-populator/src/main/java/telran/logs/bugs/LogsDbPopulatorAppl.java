package telran.logs.bugs;

import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;

@SpringBootApplication
public class LogsDbPopulatorAppl {

	@Autowired
	LogsDbRepo consumerLogs;

	@Autowired
	StreamBridge streamBridge;

	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;

	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorAppl.class);

	public static void main(String[] args) {
		try {
			SpringApplication.run(LogsDbPopulatorAppl.class, args);
			LOG.info("Microservice {} is started", LogsDbPopulatorAppl.class);
		} catch (Exception e) {
			LOG.error("Microservice {} faild to start because: {}", LogsDbPopulatorAppl.class, e.getMessage());
		}

	}

	@Bean
	Consumer<LogDto> getLogDtoCounsumer() {
		return this::takeLogDto;
	}

	@Autowired
	Validator validator;

	public void takeLogDto(LogDto logDto) {
		LOG.debug("recived log {}", logDto);

		Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);

		if (!violations.isEmpty()) {
			StringBuilder b = new StringBuilder();
			violations.forEach(b::append);
			logDto = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, LogsDbPopulatorAppl.class.toString(), 0,
					b.toString());
			consumerLogs.save(new LogDoc(logDto));
			LOG.debug("saved with exception because: {}", b);
		} else {

			consumerLogs.save(new LogDoc(logDto));
			LOG.info("saved new log {}", logDto);
		}

	}
}
