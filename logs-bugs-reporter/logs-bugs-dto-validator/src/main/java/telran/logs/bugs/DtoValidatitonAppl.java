package telran.logs.bugs;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.services.IDtoValidatorService;

@SpringBootApplication
public class DtoValidatitonAppl {

	public static void main(String[] args) {
		SpringApplication.run(DtoValidatitonAppl.class, args);
	}

	@Autowired
	IDtoValidatorService dtoValidatorService;

	@Bean
	Consumer<LogDto> getDto() {
		return dtoValidatorService::validateDto;
	}

}
