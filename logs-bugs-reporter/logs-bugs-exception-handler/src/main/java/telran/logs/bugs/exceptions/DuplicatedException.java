/**
 * 
 */
package telran.logs.bugs.exceptions;

/**
 * @author Alex Shtilman Feb 28, 2021
 *
 */
@SuppressWarnings("serial")
public class DuplicatedException extends RuntimeException {
	public DuplicatedException(String message) {
		super(message);
	}
}
