/**
 * 
 */
package telran.logs.bugs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author Alex Shtilman Mar 26, 2021
 *
 */
@SpringBootApplication
@EnableEurekaClient
public class AccountProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountProviderApplication.class, args);
	}

}
