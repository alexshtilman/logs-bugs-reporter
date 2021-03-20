/**
 * 
 */
package telran.security.accounting.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.logs.bugs.exceptions.NotFoundException;
import telran.security.accounting.dto.AccountRequest;
import telran.security.accounting.dto.AccountResponse;
import telran.security.accounting.mongo.documents.AccountDoc;
import telran.security.accounting.mongo.repo.AccountRepository;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
@Service
public class AccountingManagementImpl implements AccountingManagement {


	/**
	 * 
	 */
	private static final String PROTECTED_PASSWORD = "********";

	private static final String SECURITY_METHOD = "{noop}";

	@Autowired
	AccountRepository accountRepo;

	@Override
	public AccountResponse addAccount(AccountRequest accountDto) {
		long expirationTimestamp = (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(accountDto.expired)) / 1000l;
		long activationTimestamp = System.currentTimeMillis() / 1000l;

		AccountDoc doc = new AccountDoc(accountDto.username, SECURITY_METHOD + accountDto.password, activationTimestamp,
				expirationTimestamp,
				accountDto.roles);
		accountRepo.save(doc);
		return new AccountResponse(accountDto.username, SECURITY_METHOD + accountDto.password, accountDto.roles,
				expirationTimestamp);
	}

	@Override
	public void deleteAccount(String username) {
		Long result = accountRepo.deleteByUsername(username);
		if (result == 0) {
			throw new NotFoundException(username + " not found!");
		}
	}

	@Override
	public AccountResponse getAccount(String username) {
		return docToResponce(accountRepo.findByUsername(username));
	}

	@Override
	public AccountResponse updatePassword(String username, String password) {
		AccountDoc doc = accountRepo.updatePasswordByUserName(username, SECURITY_METHOD + password);
		if (doc == null) {
			throw new NotFoundException(username + " not found!");
		}
		return docToResponce(doc);
	}

	@Override
	public AccountResponse addRole(String username, String role) {
		AccountDoc doc = accountRepo.addRoleByUserName(username, role);
		if (doc == null) {
			throw new NotFoundException(username + " not found!");
		}
		doc.setPassword(PROTECTED_PASSWORD);
		return docToResponce(doc);
	}

	@Override
	public AccountResponse removeRole(String username, String role) {
		AccountDoc doc = accountRepo.removeRoleByUserName(username, role);
		if (doc == null) {
			throw new NotFoundException(username + " not found!");
		}
		doc.setPassword(PROTECTED_PASSWORD);
		return docToResponce(doc);
	}

	AccountResponse docToResponce(AccountDoc doc) {
		return doc == null ? null
				: new AccountResponse(doc.getUsername(), doc.getPassword(), doc.getRoles(),
						doc.getExpirationTimestamp());
	}
}
