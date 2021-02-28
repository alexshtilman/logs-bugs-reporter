/**
 * 
 */
package telran.logs.bugs.exceptions;

/**
 * @author Alex Shtilman Feb 28, 2021
 *
 */
@SuppressWarnings("serial")
public class NotFoundException extends RuntimeException {
	public NotFoundException(String message) {
		super(message);
	}
}
