package telran.security.accounting.mongo.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import telran.security.accounting.dto.AccountRequest;

@Document(collection = "accounts")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AccountDocument {

	@Id
	String username;
	String password;
	String[] roles;
	long activationTimestamp;
	long expirationTimestamp;

	public AccountDocument(AccountRequest dto) {
		long activation = System.currentTimeMillis() / 1000;
		this.username = dto.username;
		this.password = dto.password;
		this.roles = dto.roles;
		this.activationTimestamp = activation;
		this.expirationTimestamp = activation + dto.expirationPeriodMinutes * 60;
	}
}
