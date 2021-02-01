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

	void oppenBugMethod(LogDto logDto) {

		log.debug("recived log {}", logDto);

		Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);

		if (!violations.isEmpty()) {
			List<String> erorrs = new ArrayList<>();

			violations.forEach(cv -> erorrs.add("{\"" + cv.getPropertyPath() + "\": \"" + cv.getMessage() + "\"}"));
			logDto = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, BugsOppenningService.class.toString(), 0,
					erorrs.toString());
			log.debug("saved with exception because: {}", erorrs);
			streamBridge.send(bindingName, logDto);
		}
		if (logDto.logType != null && logDto.logType != LogType.NO_EXCEPTION) {
			LocalDate dateOppen = LocalDate.now(); // 1.2.1
			LocalDate dateClose = null;// 1.2.2
			EnumMap<LogType, Seriosness> logType = new EnumMap<>(LogType.class);
			logType.put(LogType.AUTHENTICATION_EXCEPTION, Seriosness.BLOCKING);
			logType.put(LogType.AUTHORIZATION_EXCEPTION, Seriosness.CRITICAL);
			logType.put(LogType.SERVER_EXCEPTION, Seriosness.CRITICAL);

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
				Programmer defaultProgrammer = new Programmer(-1, "DefaultProgrammer");
				programmersRepo.save(defaultProgrammer);
				programmer = defaultProgrammer;
				bugStatus = BugStatus.OPEND;
			}

			OppeningMethod oppeningMethod = OppeningMethod.AUTOMATIC;// 1.2.5
			String description = String.format("%s, %s", logDto.logType, logDto.result);// 1.2.6

			bugsRepo.save(
					new Bug(description, dateOppen, dateClose, bugStatus, seriosness, oppeningMethod, programmer));

		} else {
			log.debug("log is ignored");
		}

	}
}
