package telran.logs.bugs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author Alex Shtilman Mar 8, 2021
 *
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigurationServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConfigurationServiceApplication.class, args);
	}
}
