package telran.security.accounting.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class AccountPassword {
	@NotEmpty
	public String username;
	@Size(min = 8)
	public String password;

	public AccountPassword(@NotEmpty String username, @Size(min = 8) String password) {
		super();
		this.username = username;
		this.password = password;
	}

}
