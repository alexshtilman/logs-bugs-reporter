package telran.security.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
@ComponentScan({ "telran.logs.bugs", "telran.security" })
@SpringBootApplication
public class AccountingmanagementAppl {
	public static void main(String[] args) {
		SpringApplication.run(AccountingmanagementAppl.class, args);
	}

}
