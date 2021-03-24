package telran.security.accounting.controllers;

import static telran.security.accounting.api.ApiConstants.ACCOUNT;
import static telran.security.accounting.api.ApiConstants.ADD;
import static telran.security.accounting.api.ApiConstants.ASSING;
import static telran.security.accounting.api.ApiConstants.CLEAR;
import static telran.security.accounting.api.ApiConstants.ID;
import static telran.security.accounting.api.ApiConstants.PASSWORD;
import static telran.security.accounting.api.ApiConstants.ROLE;
import static telran.security.accounting.api.ApiConstants.UPDATE;

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
@Validated
@Log4j2
@RequestMapping(ACCOUNT)
public class AccountingManagementController {
	@Autowired
	AccountingManagement accountingService;

	@GetMapping(ID)
	AccountResponse getAccount(@PathVariable(name = "id") @NotEmpty String username) {
		AccountResponse responce = accountingService.getAccount(username);
		log.debug("getAccount: {}", responce);
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

		accountingService.deleteAccount(username);
	}

	@PutMapping(ROLE + ASSING)
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
