package telran.logs.bugs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.interfaces.LogsInfo;

@RestController
@Log4j2
public class LogsInfoController {
	@Autowired
	LogsInfo logsInfo;

	@GetMapping(value = "/logs", produces = "application/stream+json")
	public Flux<LogDto> getAllLogs() {
		Flux<LogDto> result = logsInfo.getAllLogs();
		log.debug("logs was sent");
		return result;
	}

	@GetMapping(value = "/logs/{type}", produces = "application/stream+json")
	public Flux<LogDto> getLogsTypes(@PathVariable(name = "type") LogType logType) {
		Flux<LogDto> result = logsInfo.getLogsTypes(logType);
		log.debug("logs was sent");
		return result;
	}

	@GetMapping(value = "/logs/exceptions", produces = "application/stream+json")
	public Flux<LogDto> getExceptions() {
		Flux<LogDto> result = logsInfo.getAllExceptions();
		log.debug("logs was sent");
		return result;
	}
}
