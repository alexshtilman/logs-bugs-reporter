package telran.security.accounting.dto;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Builder
public class AccountRole {
	@NotEmpty
public String username;
	@NotEmpty
public String role;
}
