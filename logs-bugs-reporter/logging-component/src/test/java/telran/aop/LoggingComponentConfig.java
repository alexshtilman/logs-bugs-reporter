/**
 * 
 */
package telran.aop;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author Alex Shtilman Apr 16, 2021
 *
 */
@TestConfiguration
public class LoggingComponentConfig {

	@Bean
	public LoggingComponent loggingComponent() {
		return new LoggingComponent();
	}

}
