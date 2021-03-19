/**
 * 
 */
package telran.security.accounting.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class AccountRequest {
	@NotEmpty
	public String username;
	@Length(min = 8)
	public String password;
	public String[] roles;
	@Min(1)
	public int expired; // Expired period in minutes (positive number)
}
