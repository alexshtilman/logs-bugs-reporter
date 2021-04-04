package telran.security.accounting.controllers;

import static telran.security.accounting.api.Constants.ACCOUNTS_CONTROLLER;
import static telran.security.accounting.api.Constants.ACTIVATED;
import static telran.security.accounting.api.Constants.ADD;
import static telran.security.accounting.api.Constants.ASSIGN;
import static telran.security.accounting.api.Constants.CLEAR;
import static telran.security.accounting.api.Constants.ID;
import static telran.security.accounting.api.Constants.PASSWORD;
import static telran.security.accounting.api.Constants.ROLE;
import static telran.security.accounting.api.Constants.UPDATE;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import telran.security.accounting.dto.AccountPassword;
import telran.security.accounting.dto.AccountRequest;
import telran.security.accounting.dto.AccountResponse;
import telran.security.accounting.dto.AccountRole;
import telran.security.accounting.service.AccountingManagement;

@RestController
@RequestMapping(ACCOUNTS_CONTROLLER)
@Log4j2
@Validated
public class AccountingManagementController {
	@Autowired
	AccountingManagement accountingService;

	@GetMapping(ID)
	AccountResponse getAccount(@PathVariable(name = "id") @NotEmpty String username) {
		AccountResponse responce = accountingService.getAccount(username);
		log.debug("getAccount: {}", responce);
		return responce;
	}

	@GetMapping(ACTIVATED)
	List<AccountResponse> getActivatedAccounts() {
		List<AccountResponse> responce = accountingService.getActivatedAccounts();
		log.debug("getActivatedAccounts: {}", responce);
		return responce;
	}

	@PostMapping(ADD)
	AccountResponse addAccount(@RequestBody @Valid AccountRequest account) {
		AccountResponse responce = accountingService.addAccount(account);
		log.debug("addAccount: {}", responce);
		return responce;
	}

	@DeleteMapping(ID)
	void deleteAccount(@PathVariable(name = "id") @NotEmpty String username) {
		log.debug("deleteAccount: {}", username);
		accountingService.deleteAccount(username);
	}

	@PutMapping(ROLE + ASSIGN)
	AccountResponse addRole(@RequestBody @Valid AccountRole accountRole) {
		AccountResponse responce = accountingService.addRole(accountRole.username, accountRole.role);
		log.debug("addRole: {}", responce);
		return responce;
	}

	@PutMapping(ROLE + CLEAR)
	AccountResponse removeRole(@RequestBody @Valid AccountRole accountRole) {
		AccountResponse responce = accountingService.removeRole(accountRole.username, accountRole.role);
		log.debug("removeRole: {}", responce);
		return responce;
	}

	@PutMapping(UPDATE + PASSWORD)
	AccountResponse updatePassword(@RequestBody @Valid AccountPassword accountPassword) {
		AccountResponse responce = accountingService.updatePassword(accountPassword.username, accountPassword.password);
		log.debug("updatePassword: {}", responce);
		return responce;
	}
}
