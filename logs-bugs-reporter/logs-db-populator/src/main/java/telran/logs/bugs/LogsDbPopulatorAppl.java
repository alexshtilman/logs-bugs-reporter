package telran.logs.bugs;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.mongo.repo.LogsRepo;

@SpringBootApplication
@Log4j2
public class LogsDbPopulatorAppl {

	@Autowired
	LogsRepo consumerLogs;

	public static void main(String[] args) {

		SpringApplication.run(LogsDbPopulatorAppl.class, args);

	}

	@Bean
	Consumer<LogDto> getLogDtoCounsumer() {
		return this::takeLogDto;
	}

	public void takeLogDto(LogDto logDto) {
		log.debug("recived log {}", logDto);
		consumerLogs.save(new LogDoc(logDto));
	}
}
