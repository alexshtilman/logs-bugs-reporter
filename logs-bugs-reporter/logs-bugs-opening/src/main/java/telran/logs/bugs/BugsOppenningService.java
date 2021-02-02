package telran.logs.bugs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.BugStatus;
import telran.logs.bugs.jpa.entities.OppeningMethod;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.entities.Seriosness;

@Service
@Log4j2
public class BugsOppenningService {
	@Autowired
	ArtifactRepo artifactRepo;

	@Autowired
	BugsRepo bugsRepo;

	@Autowired
	ProgrammersRepo programmersRepo;

	@Autowired
	Validator validator;

	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;
	@Autowired
	StreamBridge streamBridge;

	@Bean
	Consumer<LogDto> oppenBug() {
		return this::oppenBugMethod;
	}

	EnumMap<LogType, Seriosness> logType = new EnumMap<>(LogType.class);

	public BugsOppenningService() {
		logType.put(LogType.AUTHENTICATION_EXCEPTION, Seriosness.BLOCKING);
		logType.put(LogType.AUTHORIZATION_EXCEPTION, Seriosness.CRITICAL);
		logType.put(LogType.SERVER_EXCEPTION, Seriosness.CRITICAL);
	}

	void oppenBugMethod(LogDto logDto) {
		log.debug("recived log {}", logDto);
		logDto = validateDto(logDto);
		if (logDto.logType != null && logDto.logType != LogType.NO_EXCEPTION) {
			LocalDate dateOppen = LocalDate.now(); // 1.2.1
			LocalDate dateClose = null;// 1.2.2
			OppeningMethod oppeningMethod = OppeningMethod.AUTOMATIC;// 1.2.5
			Seriosness seriosness;// 1.2.3
			if (logType.get(logDto.logType) != null)
				seriosness = logType.get(logDto.logType);
			else
				seriosness = Seriosness.MINOR;
			BugStatus bugStatus;// 1.2.4
			Programmer programmer;
			Artifact artifact = artifactRepo.findById(logDto.artifact).orElse(null);
			if (artifact != null) {
				// 1.2.4.1
				programmer = artifact.getProgrammer();
				bugStatus = BugStatus.ASSIGNED;
			} else {
				// 1.2.4.2
				log.warn("the programmer related to the artifact {} doesn’t exist", logDto.artifact);
				programmer = null;
				bugStatus = BugStatus.OPEND;
			}
			String description = String.format("%s, %s", logDto.logType, logDto.result);// 1.2.6
			bugsRepo.save(
					new Bug(description, dateOppen, dateClose, bugStatus, seriosness, oppeningMethod, programmer));
		} else {
			log.debug("log is ignored");
		}
	}

	LogDto validateDto(LogDto logDto) {
		Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);
		List<String> erorrs = new ArrayList<>();
		if (!violations.isEmpty()) {
			violations.forEach(cv -> erorrs.add("{" + cv.getPropertyPath() + ": '" + cv.getMessage() + "'}"));
			log.debug("saved with exception because: {}", erorrs);
			streamBridge.send(bindingName, logDto);
			return new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, BugsOppenningService.class.toString(), 0,
					erorrs.toString());
		}
		return logDto;
	}
}
