package telran.security.accounting.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import telran.security.accounting.dto.AccountRequest;
import telran.security.accounting.dto.AccountResponse;
import telran.security.accounting.mongo.documents.AccountDocument;
import telran.security.accounting.repo.AccountRepository;

@Service
public class AccountingManagementImpl implements AccountingManagement {
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public AccountResponse addAccount(AccountRequest accountDto) {
		if (accountRepository.existsById(accountDto.username)) {
			throw new RuntimeException(accountDto.username + " already exists");
		}
		AccountDocument account = new AccountDocument(accountDto);
		accountRepository.save(account);

		return toResponse(account);
	}

	private String getStoredPassword(String password) {

		return passwordEncoder.encode(password);
	}

	private AccountResponse toResponse(AccountDocument account) {

		return new AccountResponse(account.getUsername(), account.getPassword(), account.getRoles(),
				account.getExpirationTimestamp());
	}

	@Override
	public void deleteAccount(String username) {
		if (!accountRepository.existsById(username)) {
			throw new RuntimeException(username + " doesn't exist");
		}
		accountRepository.deleteById(username);

	}

	@Override
	public AccountResponse getAccount(String username) {
		AccountDocument account = accountRepository.findById(username).orElse(null);
		return account == null || account.getExpirationTimestamp() < System.currentTimeMillis() / 1000 ? null
				: toResponse(account);
	}

	@Override
	public AccountResponse updatePassword(String username, String password) {
		AccountDocument account = accountRepository.findById(username).orElse(null);
		if (account == null) {
			throw new RuntimeException(username + " doesn't exist");
		}
		if (samePasswords(password, account.getPassword())) {
			throw new RuntimeException("the same password");
		}
		String storedPassword = getStoredPassword(password);
		long newActivation = System.currentTimeMillis() / 1000;
		AccountDocument res = accountRepository.updatePassword(username, storedPassword, newActivation,
				getNewExpiration(newActivation, account));

		if (res == null) {
			throw new RuntimeException("account not updated");
		}
		return toResponseHiddenPassword(res);
	}

	private AccountResponse toResponseHiddenPassword(AccountDocument account) {
		AccountResponse res = toResponse(account);
		res.password = "*".repeat(8);
		return res;
	}

	private long getNewExpiration(long newActivation, AccountDocument account) {
		long oldExpiration = account.getExpirationTimestamp();
		return oldExpiration - account.getActivationTimestamp() + newActivation;
	}

	private boolean samePasswords(String newPassword, String oldPassword) {
		return passwordEncoder.matches(newPassword, oldPassword);
	}

	@Override
	public AccountResponse addRole(String username, String role) {
		AccountDocument account = accountRepository.addRole(username, role);
		if (account == null) {
			throw new RuntimeException(username + " doesn't exist");
		}
		return toResponseHiddenPassword(account);
	}

	@Override
	public AccountResponse removeRole(String username, String role) {
		AccountDocument account = accountRepository.removeRole(username, role);
		if (account == null) {
			throw new RuntimeException(username + " doesn't exist");
		}
		return toResponseHiddenPassword(account);
	}

	@Override
	public List<AccountResponse> getActivatedAccounts() {
		List<AccountDocument> activatedAccounts = accountRepository
				.findByExpirationTimestampGreaterThan(System.currentTimeMillis() / 1000);
		return activatedAccounts.isEmpty() ? Collections.emptyList() : toListAccountResponse(activatedAccounts);
	}

	private List<AccountResponse> toListAccountResponse(List<AccountDocument> accounts) {

		return accounts.stream().map(this::toResponse).collect(Collectors.toList());
	}

}
