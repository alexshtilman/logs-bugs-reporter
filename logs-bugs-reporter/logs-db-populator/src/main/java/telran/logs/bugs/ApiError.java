package telran.logs.bugs;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class ApiError {
	private String field;
	private String message;
	private String invalidValue;
}
