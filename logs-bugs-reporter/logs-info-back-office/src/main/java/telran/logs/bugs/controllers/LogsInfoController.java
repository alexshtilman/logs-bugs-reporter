package telran.logs.bugs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.interfaces.LogsInfo;

@RestController
@Log4j2
@RequestMapping("/logs")
public class LogsInfoController {
	@Autowired
	LogsInfo logsInfo;

	@GetMapping(value = "/all", produces = "application/stream+json")
	public Flux<LogDto> getAllLogs() {
		Flux<LogDto> result = logsInfo.getAllLogs();
		log.debug("all logs was sent");
		return result;
	}

	@GetMapping(value = "/by_type", produces = "application/stream+json")
	public Flux<LogDto> getLogsTypes(@RequestParam(name = "type") LogType logType) {
		Flux<LogDto> result = logsInfo.getLogsTypes(logType);
		log.debug("logs by {} was sent", logType);
		return result;
	}

	@GetMapping(value = "/exceptions", produces = "application/stream+json")
	public Flux<LogDto> getExceptions() {
		Flux<LogDto> result = logsInfo.getAllExceptions();
		log.debug("only exceptions was sent");
		return result;
	}
}
