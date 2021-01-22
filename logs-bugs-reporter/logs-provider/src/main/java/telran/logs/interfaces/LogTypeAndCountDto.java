package telran.logs.interfaces;

public class LogTypeAndCountDto {

    public String logType;
    public int count;

    public LogTypeAndCountDto(String logType, int count) {
	super();
	this.logType = logType;
	this.count = count;
    }

    public LogTypeAndCountDto() {

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
