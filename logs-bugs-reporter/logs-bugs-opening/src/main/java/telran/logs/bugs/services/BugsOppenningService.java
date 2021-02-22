package telran.logs.bugs.services;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.function.Consumer;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.OpenningMethod;
import telran.logs.bugs.dto.Seriousness;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.repositories.ArtifactRepo;
import telran.logs.bugs.repositories.BugsRepo;
import telran.logs.bugs.repositories.ProgrammersRepo;

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

	EnumMap<LogType, Seriousness> logType = new EnumMap<>(LogType.class);

	public BugsOppenningService() {
		logType.put(LogType.AUTHENTICATION_EXCEPTION, Seriousness.BLOCKING);
		logType.put(LogType.AUTHORIZATION_EXCEPTION, Seriousness.CRITICAL);
		logType.put(LogType.SERVER_EXCEPTION, Seriousness.CRITICAL);
	}

	void oppenBugMethod(LogDto logDto) {
		log.debug("BugsOppenningService recived log {}", logDto);
		LocalDate dateOppen = LocalDate.now(); // 1.2.1
		LocalDate dateClose = null;// 1.2.2
		OpenningMethod oppeningMethod = OpenningMethod.AUTOMATIC;// 1.2.5
		Seriousness seriosness;// 1.2.3
		if (logType.get(logDto.logType) != null)
			seriosness = logType.get(logDto.logType);
		else
			seriosness = Seriousness.MINOR;
		BugStatus bugStatus;// 1.2.4
		Programmer programmer;
		Artifact artifact = artifactRepo.findById(logDto.artifact).orElse(null);
		if (artifact != null) {
			// 1.2.4.1
			programmer = artifact.getProgrammer();
			bugStatus = BugStatus.ASSIGNED;
		} else {
			// 1.2.4.2
			log.warn("Programmer related to the artifact {} doesn’t exist", logDto.artifact);
			programmer = null;
			bugStatus = BugStatus.OPEND;
		}
		String description = String.format("%s, %s", logDto.logType, logDto.result);// 1.2.6

		bugsRepo.save(new Bug(description, dateOppen, dateClose, bugStatus, seriosness, oppeningMethod, programmer));
		log.debug("New Bug report was created, seriosness:{}, bugStatus: {}, assignet to: {}", seriosness, bugStatus,
				programmer == null ? "no assigned" : programmer);

	}

}
