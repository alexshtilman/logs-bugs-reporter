package telran.logs.bugs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LogsInfoAppl {

	public static void main(String[] args) {
		SpringApplication.run(LogsInfoAppl.class, args);
	}

}
