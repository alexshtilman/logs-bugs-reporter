package telran.logs.providers;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.mongo.doc.LogDoc;

@SpringBootApplication
public class LogsDbAppl {

    @Autowired
    LogsDbRepo randomLogs;

    public static void main(String[] args) {
	SpringApplication.run(LogsDbAppl.class, args);
    }

    @Bean
    Consumer<LogDto> getLogDtoCounsumer() {
	return this::takeLogDto;
    }

    public void takeLogDto(LogDto logDto) {
	randomLogs.save(new LogDoc(logDto));
    }

//    @Bean
//    Supplier<LogDto> randomLogsProvider() {
//	return this::findAll;
//    }
//
//    public LogDto findAll() {
//	List<LogDoc> docs = randomLogs.findAll();
//	return docs.get(0).getLogDto();
//    }
}
