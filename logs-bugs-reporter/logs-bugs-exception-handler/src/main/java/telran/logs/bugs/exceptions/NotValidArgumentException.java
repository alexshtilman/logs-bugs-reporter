/**
 * 
 */
package telran.logs.bugs.exceptions;

/**
 * @author Alex Shtilman Mar 1, 2021
 *
 */
@SuppressWarnings("serial")
public class NotValidArgumentException extends RuntimeException {
	public NotValidArgumentException(String message) {
		super(message);
	}
}
