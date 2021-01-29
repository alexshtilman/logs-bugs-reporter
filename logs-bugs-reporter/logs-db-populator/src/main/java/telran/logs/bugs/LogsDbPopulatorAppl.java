package telran.logs.bugs;

import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.mongo.doc.LogDoc;

@SpringBootApplication
public class LogsDbPopulatorAppl {

	@Autowired
	LogsDbRepo consumerLogs;

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
		Set<ConstraintViolation<LogDto>> validations = validator.validate(logDto);

		if (!validations.isEmpty()) {
			//TODO Auto-generated method stub
			System.out.println(validations.iterator().next().getMessage());
		} else {
			consumerLogs.save(new LogDoc(logDto));
		}

	}
}
