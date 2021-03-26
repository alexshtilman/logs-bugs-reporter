package telran.security.accounting.dto;
import javax.validation.constraints.*;
public class AccountPassword {
	@NotEmpty
	public String username;
	@Size(min=8)
	public String password;
	public AccountPassword(@NotEmpty String username, @Size(min = 8) String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
}
