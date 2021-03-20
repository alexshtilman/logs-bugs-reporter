/**
 * 
 */
package telran.security.accounting.mongo.documents;

import java.util.Arrays;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
@Document(collection = "accounts")
public class AccountDoc {
	@Id
	String username;
	String password;
	long activationTimestamp;
	long expirationTimestamp;
	String[] roles;

	public AccountDoc(String username, String password, long activationTimestamp, long expirationTimestamp,
			String[] roles) {
		super();
		this.username = username;
		this.password = password;
		this.activationTimestamp = activationTimestamp;
		this.expirationTimestamp = expirationTimestamp;
		this.roles = roles;
	}

	public AccountDoc() {

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getActivationTimestamp() {
		return activationTimestamp;
	}

	public void setActivationTimestamp(long activationTimestamp) {
		this.activationTimestamp = activationTimestamp;
	}

	public long getExpirationTimestamp() {
		return expirationTimestamp;
	}

	public void setExpirationTimestamp(long expirationTimestamp) {
		this.expirationTimestamp = expirationTimestamp;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "AccountDoc [username=" + username + ", password=" + password + ", activationTimestamp="
				+ activationTimestamp + ", expirationTimestamp=" + expirationTimestamp + ", roles="
				+ Arrays.toString(roles) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (activationTimestamp ^ (activationTimestamp >>> 32));
		result = prime * result + (int) (expirationTimestamp ^ (expirationTimestamp >>> 32));
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + Arrays.hashCode(roles);
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		AccountDoc other = (AccountDoc) obj;
		if (activationTimestamp != other.activationTimestamp)
			return false;
		if (expirationTimestamp != other.expirationTimestamp)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (!Arrays.equals(roles, other.roles))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
