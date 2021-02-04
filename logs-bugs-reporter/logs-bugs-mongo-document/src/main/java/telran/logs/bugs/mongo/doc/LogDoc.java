package telran.logs.bugs.mongo.doc;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NoArgsConstructor;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@NoArgsConstructor
@Document(collection = "logs")
public class LogDoc {
	@Id
	ObjectId id;

	public ObjectId getId() {
		return id;
	}

	private Date dateTime;
	private LogType logType;
	private String artifact;
	private int responseTime;
	private String result;

	public LogDoc(LogDto logDto) {
		dateTime = logDto.dateTime;
		logType = logDto.logType;
		artifact = logDto.artifact;
		responseTime = logDto.responseTime;
		result = logDto.result;
	}

	public LogDto getLogDto() {
		return new LogDto(dateTime, logType, artifact, responseTime, result);
	}

	public LogDoc(Date dateTime, LogType logType, String artifact, int responseTime, String result) {
		super();
		this.dateTime = dateTime;
		this.logType = logType;
		this.artifact = artifact;
		this.responseTime = responseTime;
		this.result = result;
	}
}
