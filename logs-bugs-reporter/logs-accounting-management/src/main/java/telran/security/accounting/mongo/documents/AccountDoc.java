/**
 * 
 */
package telran.security.accounting.mongo.documents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
@Document(collection = "accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class AccountDoc implements UserDetails {
	@Id
	String username;
	String password;
	long activationTimestamp;
	long expirationTimestamp;

	@Builder.Default()
	@DBRef
	private List<Role> roles = new ArrayList<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	public String[] getRoles() {
		List<String> result = new ArrayList<>();
		roles.forEach(role -> result.add(role.getAuthority()));
		return result.toArray(new String[0]);
	}
	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}


}
