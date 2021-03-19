/**
 * 
 */
package telran.security.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class AccountResponse {
	String username;
	String password;
	String[] roles;
	long expiredTimeStamp; // Expiration timestamp in the seconds (number seconds from 1970-01-01)
}
