package telran.logs.bugs;

public class ApiError {
	private String field;
	private String message;
	private String invalidValue;

	public ApiError(String field, String message, String invalidValue) {

		this.field = field;
		this.message = message;
		this.invalidValue = invalidValue;
	}

	@Override
	public String toString() {
		return "{field:'" + field + "', message:'" + message + "', invalidValue:'" + invalidValue + "'}";
	}
}
