package telran.logs.bugs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for microservice that validate's
 * {@link telran.logs.bugs.dto.LogDto}
 * 
 * @author Shtilman Alex
 */
@SpringBootApplication
public class LogsAnalyzerAppl {

	public static void main(String[] args) {
		SpringApplication.run(LogsAnalyzerAppl.class, args);
	}
}
