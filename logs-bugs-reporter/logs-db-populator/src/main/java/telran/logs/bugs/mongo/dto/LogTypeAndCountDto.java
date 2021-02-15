package telran.logs.bugs.mongo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class LogTypeAndCountDto {
	public final String logType;
	public final int count;
}
