package telran.security.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({ "telran.logs.bugs", "telran.security" })
@SpringBootApplication
public class AccountingManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountingManagementApplication.class, args);
	}

}
