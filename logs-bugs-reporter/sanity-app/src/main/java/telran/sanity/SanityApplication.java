package telran.sanity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Alex Shtilman Apr 14, 2021
 *
 */
@SpringBootApplication
@ComponentScan({ "telran.aop", "telran.sanity.controller", "telran.logs.bugs.controllers" })
public class SanityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SanityApplication.class, args);
	}

}
