package telran.security.accounting.dto;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountPassword {
	@NotEmpty
	public String username;
	@Size(min=8)
	public String password;
	
}
