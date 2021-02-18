package telran.logs.bugs.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
public class LogTypeAndCountDto {
	public static final String LOG_TYPE = "logType";
	public final LogType logType;
	public final int count;
}
