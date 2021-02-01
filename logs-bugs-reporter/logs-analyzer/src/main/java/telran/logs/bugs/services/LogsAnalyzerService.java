package telran.logs.bugs.services;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@Service
@Log4j2
public class LogsAnalyzerService {
	@Autowired
	StreamBridge streamBridge;

	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;

	@Bean
	Consumer<LogDto> getAnalyzerBean() {
		return this::analyzerMethod;
	}

	void analyzerMethod(LogDto logDto) {
		log.debug("recived log {}", logDto);
		if (logDto.logType != null && logDto.logType != LogType.NO_EXCEPTION) {
			streamBridge.send(bindingName, logDto);
		}
	}
}
