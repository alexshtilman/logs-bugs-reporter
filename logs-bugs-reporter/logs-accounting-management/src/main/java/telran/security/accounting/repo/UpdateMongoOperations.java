package telran.security.accounting.repo;

import java.util.List;

import telran.security.accounting.dto.AccountResponse;
import telran.security.accounting.mongo.documents.AccountDocument;

public interface UpdateMongoOperations {
	AccountDocument updatePassword(String username, String newPassword, long newActivationlong, long newExpiration);

	AccountDocument addRole(String username, String role);

	AccountDocument removeRole(String username, String role);

	List<AccountResponse> getActivatedAccounts();
}
