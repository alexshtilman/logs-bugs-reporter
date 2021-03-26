/**
 * 
 */
package telran.logs.bugs.controllers;

import static telran.security.accounting.api.Constants.ACCOUNTS;
import static telran.security.accounting.api.Constants.ACTIVATED;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import telran.logs.bugs.repo.AccountRepository;
import telran.security.accounting.dto.AccountResponse;
import telran.security.accounting.mongo.documents.AccountDocument;

/**
 * @author Alex Shtilman Mar 26, 2021
 *
 */
@RestController
public class AccountProviderController {

	@Autowired
	AccountRepository accountRepository;

	@RequestMapping(ACCOUNTS + ACTIVATED)
	public List<AccountResponse> getActivatedAccounts() {
		List<AccountDocument> activatedAccounts = accountRepository
				.findByExpirationTimestampGreaterThan(System.currentTimeMillis() / 1000);
		return activatedAccounts.isEmpty() ? Collections.emptyList() : toListAccountResponse(activatedAccounts);
	}

	private List<AccountResponse> toListAccountResponse(List<AccountDocument> accounts) {
		return accounts.stream().map(this::toResponse).collect(Collectors.toList());
	}

	private AccountResponse toResponse(AccountDocument account) {
		return new AccountResponse(account.getUsername(), account.getPassword(), account.getRoles(),
				account.getExpirationTimestamp());
	}
}
