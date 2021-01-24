package telran.logs.bugs;

import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;

@SpringBootApplication
public class LogsDbPopulatorAppl {

    public static void main(String[] args) {
	SpringApplication.run(LogsDbPopulatorAppl.class);
    }

    @Bean
    Consumer<LogDto> getLogDtoCounsumer() {
	return this::takeLogDto;
    }

    public void takeLogDto(LogDto logDto) {
	// TODO from LogDto -> new MongoDataBaseDOcument
	// save to db
	System.out.println(logDto);
    }
}
