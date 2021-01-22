package telran.logs.interfaces;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "logdto")
public class LogTypeAndCountDto {
    @Id
    ObjectId id;

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
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + count;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	result = prime * result + ((logType == null) ? 0 : logType.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	LogTypeAndCountDto other = (LogTypeAndCountDto) obj;
	if (count != other.count)
	    return false;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	if (logType == null) {
	    if (other.logType != null)
		return false;
	} else if (!logType.equals(other.logType))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return logType + ":" + count;
    }

}
