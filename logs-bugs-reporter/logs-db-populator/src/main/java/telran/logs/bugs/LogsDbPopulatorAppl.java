package telran.logs.bugs;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.mongo.doc.LogDoc;


@SpringBootApplication
public class LogsDbPopulatorAppl {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LogsDbRepo consumerLogs;

    public static void main(String[] args) {

	SpringApplication.run(LogsDbPopulatorAppl.class, args);
    }

    @Bean
    Consumer<LogDto> getLogDtoCounsumer() {
	return this::takeLogDto;
    }

    public void takeLogDto(LogDto logDto) {
	try {
		logDto.validateInput();
		consumerLogs.save(new LogDoc(logDto));
		log.info("1 record inserted");
	    } catch (Exception e) {
		log.error(e.getMessage());
	    }


    }
}
