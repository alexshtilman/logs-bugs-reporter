package telran.logs.bugs.dto;

import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LogDto {
    @NotNull
    public Date dateTime;

    @Override
    public String toString() {
	return "LogDto [dateTime=" + dateTime + ", logType=" + logType + ", artifact=" + artifact + ", responseTime="
		+ responseTime + ", result=" + result + "]";
    }

    @NotNull
    public LogType logType;

    @NotEmpty(message = "`artifact` field is can't be empty")
    public String artifact;

    public int responseTime;
    public String result;

    public LogDto(@NotNull Date dateTime, @NotNull LogType logType, @NotEmpty String artifact,
	    int responseTime,
	    String result) {
	super();
	this.dateTime = dateTime;
	this.logType = logType;
	this.artifact = artifact;
	this.responseTime = responseTime;
	this.result = result;
    }

    public LogType getLogType() {
	return logType;
    }

    public void validateInput() {
	ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	Validator validator = factory.getValidator();
	Set<ConstraintViolation<LogDto>> violations = validator.validate(this);
	if (!violations.isEmpty()) {
	    throw new ConstraintViolationException(violations);
	}
    }
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((artifact == null) ? 0 : artifact.hashCode());
	result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
	result = prime * result + ((logType == null) ? 0 : logType.hashCode());
	result = prime * result + responseTime;
	result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
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
	LogDto other = (LogDto) obj;
	if (artifact == null) {
	    if (other.artifact != null)
		return false;
	} else if (!artifact.equals(other.artifact))
	    return false;
	if (dateTime == null) {
	    if (other.dateTime != null)
		return false;
	} else if (!dateTime.equals(other.dateTime))
	    return false;
	if (logType != other.logType)
	    return false;
	if (responseTime != other.responseTime)
	    return false;
	if (result == null) {
	    if (other.result != null)
		return false;
	} else if (!result.equals(other.result))
	    return false;
	return true;
    }
   
}
