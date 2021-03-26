package telran.security.accounting.dto;
import javax.validation.constraints.*;
public class AccountRole {
	@NotEmpty
public String username;
	@NotEmpty
public String role;
	public AccountRole(@NotEmpty String username, @NotEmpty String role) {
		super();
		this.username = username;
		this.role = role;
	}
}
