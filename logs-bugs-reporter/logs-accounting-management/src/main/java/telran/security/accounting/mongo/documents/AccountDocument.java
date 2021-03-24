package telran.security.accounting.mongo.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "accounts")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDocument {

	@Id
	String username;
	long activationTimestamp;
	String password;
	String[] roles;
	long expirationTimestamp;
}
