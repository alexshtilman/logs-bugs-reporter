/**
 * 
 */
package telran.security.accounting.dto;

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
public class AccountResponse {
	public String username;
	public String password;
	public String[] roles;
	public long expiredTimeStamp; // Expiration timestamp in the seconds (number seconds from 1970-01-01)
}
