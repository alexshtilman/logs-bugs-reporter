package telran.logs.bugs;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.components.RandomLogsComponent;
import telran.logs.bugs.dto.LogDto;

@SpringBootApplication
@Log4j2
public class RandomLogsAppl {

	@Autowired
	RandomLogsComponent randomLogs;

	public static void main(String[] args) {
		SpringApplication.run(RandomLogsAppl.class, args);

	}

	@Bean
	Supplier<LogDto> randomLogsProvider() {

		return this::sendRandomLog;
	}

	LogDto sendRandomLog() {
		LogDto logDto = randomLogs.createRandomLog();
		log.debug("sent log: {}", logDto);
		return logDto;
	}

}
