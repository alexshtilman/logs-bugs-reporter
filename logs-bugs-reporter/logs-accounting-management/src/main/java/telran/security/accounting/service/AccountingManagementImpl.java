/**
 * 
 */
package telran.security.accounting.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import telran.logs.bugs.exceptions.DuplicatedException;
import telran.logs.bugs.exceptions.NotFoundException;
import telran.security.accounting.dto.AccountRequest;
import telran.security.accounting.dto.AccountResponse;
import telran.security.accounting.mongo.documents.AccountDoc;
import telran.security.accounting.mongo.documents.Role;
import telran.security.accounting.mongo.repo.AccountRepository;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
@Service
public class AccountingManagementImpl implements AccountingManagement {

	private static final String PROTECTED_PASSWORD = "********";

	private static final String SECURITY_METHOD = "{bcrypt}";

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public AccountResponse addAccount(AccountRequest accountDto) {
		if (accountRepo.findByUsername(accountDto.username) != null) {
			throw new DuplicatedException(accountDto.username + " already exists");
		}
		long activationTimestamp = System.currentTimeMillis() / 1000;
		long expirationTimestamp = activationTimestamp + accountDto.expired * 60;
		List<Role> roles = new ArrayList<>();
		for (String role : accountDto.roles) {
			roles.add(new Role(role));
		}
		AccountDoc account = new AccountDoc(accountDto.username, getStoredPassword(accountDto.password),
				activationTimestamp, expirationTimestamp, roles);
		accountRepo.save(account);
		return new AccountResponse(account.getUsername(), account.getPassword(), accountDto.roles,
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
		return docToResponce(accountRepo.findByUsername(username).block());
	}

	@Override
	public AccountResponse updatePassword(String username, String password) {

		AccountDoc account = accountRepo.findByUsername(username).block();
		if (account == null) {
			throw new NotFoundException(username + " doesn't exist");
		}
		if (samePasswords(password, account.getPassword())) {
			throw new RuntimeException("the same password");
		}
		String storedPassword = getStoredPassword(password);
		long newActivation = System.currentTimeMillis() / 1000;
		AccountDoc doc = accountRepo.updatePasswordByUserName(username, storedPassword, newActivation,
				getNewExpiration(newActivation, account));

		if (doc == null) {
			throw new RuntimeException("account not updated");
		}

		return docToResponce(doc);
	}

	@Override
	public AccountResponse addRole(String username, String role) {
		AccountDoc doc = accountRepo.addRoleByUserName(username, role);
		if (doc == null) {
			throw new NotFoundException(username + " not found!");
		}

		return docToResponce(doc);
	}

	@Override
	public AccountResponse removeRole(String username, String role) {
		AccountDoc doc = accountRepo.removeRoleByUserName(username, role);
		if (doc == null) {
			throw new NotFoundException(username + " not found!");
		}

		return docToResponce(doc);
	}

	AccountResponse docToResponce(AccountDoc doc) {
		return doc == null ? null
				: new AccountResponse(doc.getUsername(), doc.getPassword(), doc.getRoles(),
						doc.getExpirationTimestamp());
	}

	private long getNewExpiration(long newActivation, AccountDoc account) {
		long oldExpiration = account.getExpirationTimestamp();
		return oldExpiration - account.getActivationTimestamp() + newActivation;
	}

	private boolean samePasswords(String newPassword, String oldPassword) {

		return passwordEncoder.matches(newPassword, oldPassword);
	}

	private String getStoredPassword(String password) {
		return passwordEncoder.encode(password);
	}

}
