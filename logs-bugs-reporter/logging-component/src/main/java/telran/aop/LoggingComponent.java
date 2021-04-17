/**
 * 
 */
package telran.aop;

import telran.logs.bugs.dto.LogDto;

public interface LoggingComponent {
	void sendLog(LogDto logDto);

}
