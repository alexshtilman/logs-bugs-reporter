/**
 * 
 */
package telran.logs.bugs.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
public class ProgrammerDto {
	@Min(value = 1)
	public long id;
	@NotEmpty
	public String name;
	@Email
	public String email;
}
