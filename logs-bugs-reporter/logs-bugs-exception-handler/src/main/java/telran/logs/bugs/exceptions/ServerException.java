/**
 * 
 */
package telran.logs.bugs.exceptions;

/**
 * @author Alex Shtilman Feb 28, 2021
 *
 */
@SuppressWarnings("serial")
public class ServerException extends RuntimeException {
	public ServerException(String message) {
		super(message);
	}
}
