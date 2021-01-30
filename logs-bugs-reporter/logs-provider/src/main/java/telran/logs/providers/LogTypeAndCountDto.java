package telran.logs.providers;

public class LogTypeAndCountDto {

	public final String logType;
	public final int count;

	public LogTypeAndCountDto(String logType, int count) {
		super();
		this.logType = logType;
		this.count = count;
	}

	public LogTypeAndCountDto() {
		this.logType = "";
		this.count = 0;
	}

	public String getLogType() {
		return logType;
	}

	public int getCount() {
		return count;
	}

	@Override
	public String toString() {
		return logType + ":" + count;
	}

}
