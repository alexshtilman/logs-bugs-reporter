package telran.security.accounting.dto;

import java.util.Arrays;
import java.util.Objects;

import javax.validation.constraints.*;

public class AccountDto {
	
	@NotEmpty
public String username;
	@Size(min=8)
	public String password;
	@NotNull
	public String[] roles;
	public AccountDto(@NotEmpty String username, @Size(min = 8) String password, @NotNull String[] roles) {
		super();
		this.username = username;
		this.password = password;
		this.roles = roles;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(roles);
		result = prime * result + Objects.hash(username);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountDto other = (AccountDto) obj;
		return Arrays.equals(roles, other.roles) && Objects.equals(username, other.username);
	}

}
