package telran.security.accounting.dto;

import javax.validation.constraints.Min;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;



@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class AccountRequest extends AccountDto {
	@Min(1)
	public int expirationPeriodMinutes; // Expired period in minutes (positive number)

	public AccountRequest(String username, String password, String[] roles, @Min(1) int expirationPeriodMinutes) {
		super(username, password, roles);
		this.expirationPeriodMinutes = expirationPeriodMinutes;
	}

}
