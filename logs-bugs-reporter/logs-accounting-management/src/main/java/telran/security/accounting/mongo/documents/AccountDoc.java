/**
 * 
 */
package telran.security.accounting.mongo.documents;

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
	long timestamp;
	String[] roles;

	public AccountDoc(String username, String password, long timestamp, String[] roles) {
		super();
		this.username = username;
		this.password = password;
		this.timestamp = timestamp;
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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}
}
