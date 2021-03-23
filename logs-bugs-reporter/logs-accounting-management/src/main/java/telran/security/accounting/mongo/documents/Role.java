/**
 * 
 */
package telran.security.accounting.mongo.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Alex Shtilman Mar 23, 2021
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Document
public class Role implements GrantedAuthority {
	@Id
	private String id;

	@Override
	public String getAuthority() {
		return id;
	}
}
