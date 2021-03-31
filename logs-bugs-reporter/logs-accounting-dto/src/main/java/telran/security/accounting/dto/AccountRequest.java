package telran.security.accounting.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class AccountRequest extends AccountDto {

	public long expirationPeriodMinutes;

	public AccountRequest(@NotEmpty String username, @Size(min = 8) String password, @NotNull String[] roles,
			long expirationPeriodMinutes) {
		super(username, password, roles);
		this.expirationPeriodMinutes = expirationPeriodMinutes;
	}

}
