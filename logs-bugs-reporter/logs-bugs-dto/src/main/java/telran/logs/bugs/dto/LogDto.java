package telran.logs.bugs.dto;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Getter
@Builder
public class LogDto {
	@NotNull
	public Date dateTime;

	@NotNull
	public LogType logType;

	@NotEmpty
	public String artifact;

	public int responseTime;
	public String result;

	public LogDto(@NotNull Date dateTime, @NotNull LogType logType, @NotEmpty String artifact, int responseTime,
			String result) {
		super();
		this.dateTime = dateTime;
		this.logType = logType;
		this.artifact = artifact;
		this.responseTime = responseTime;
		this.result = result;
	}
}
