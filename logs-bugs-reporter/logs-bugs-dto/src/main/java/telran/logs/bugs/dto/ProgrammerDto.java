/**
 * 
 */
package telran.logs.bugs.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import lombok.ToString;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
@ToString
public class ProgrammerDto {
	@Min(value = 1)
	public long id;
	@NotEmpty
	public String name;
	@Email
	public String email;
}
