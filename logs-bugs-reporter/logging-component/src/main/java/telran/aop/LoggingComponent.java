/**
 * 
 */
package telran.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;

/**
 * @author Alex Shtilman Apr 13, 2021
 *
 */
@Component
@PropertySource(value = "classpath:karafka.properties")
@Log4j2
public class LoggingComponent {
	@Autowired
	StreamBridge streamBridge;

	@Value("${app-binding-name}")
	String bindingName;

	public void sendLog(LogDto logDto) {
		streamBridge.send(bindingName, logDto);
		log.debug("Log was sent to:{}", bindingName);
	}

}
