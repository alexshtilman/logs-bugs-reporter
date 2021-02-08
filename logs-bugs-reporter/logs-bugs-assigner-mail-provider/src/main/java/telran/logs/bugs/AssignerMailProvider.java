package telran.logs.bugs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AssignerMailProvider {

	public static void main(String[] args) {
		SpringApplication.run(AssignerMailProvider.class, args);
	}

	@Value("${default-assigner-email}")
	String defaultAssgnerEmail;

	@GetMapping("/get_assigner_mail")
	public String getAssignerMail() {
		return defaultAssgnerEmail;
	}
}
