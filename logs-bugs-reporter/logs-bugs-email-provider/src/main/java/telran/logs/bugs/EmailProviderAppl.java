package telran.logs.bugs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class EmailProviderAppl {

	public static void main(String[] args) {
		SpringApplication.run(EmailProviderAppl.class, args);
	}

}
