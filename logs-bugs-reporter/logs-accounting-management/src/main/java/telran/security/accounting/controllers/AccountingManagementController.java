/**
 * 
 */
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

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import telran.security.accounting.dto.AccountRequest;
import telran.security.accounting.dto.AccountResponse;
import telran.security.accounting.service.AccountingManagement;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
@RestController
@Validated
@Log4j2
@RequestMapping(ACCOUNT)
public class AccountingManagementController {
	@Autowired
	AccountingManagement managmentService;

	@GetMapping(ID)
	AccountResponse getAccount(@PathVariable(name = "id") @NotEmpty String username) {
		AccountResponse responce = managmentService.getAccount(username);
		log.debug("getAccount reponce: {}", responce);
		return responce;
	}

	@PostMapping(ADD)
	AccountResponse addAccount(@RequestBody @Valid AccountRequest accountDto) {
		AccountResponse responce = managmentService.addAccount(accountDto);
		log.debug("addAccount reponce: {}", responce);
		return responce;
	}

	@PutMapping(UPDATE + PASSWORD)
	AccountResponse updatePassword(@RequestParam(required = true) @NotEmpty String username,
			@RequestBody @Valid @Length(min = 8) String password) {
		AccountResponse responce = managmentService.updatePassword(username, password);
		log.debug("updatePassword reponce: {}", responce);
		return responce;
	}

	@PutMapping(ROLE + ASSING)
	AccountResponse assignRole(@RequestParam(required = true) @NotEmpty String username,
			@RequestParam(required = true) @NotEmpty String role) {
		AccountResponse responce = managmentService.addRole(username, role);
		log.debug("addRole reponce: {}", responce);
		return responce;
	}

	@PutMapping(ROLE + CLEAR)
	AccountResponse clearRole(@RequestParam(required = true) @NotEmpty String username,
			@RequestParam(required = true) @NotEmpty String role) {
		AccountResponse responce = managmentService.removeRole(username, role);
		log.debug("removeRole reponce: {}", responce);
		return responce;
	}

	@DeleteMapping(ID)
	void removeAccount(@PathVariable(name = "id") @NotEmpty String username) {
		log.debug("removeAccount by username: {}", username);
		managmentService.deleteAccount(username);
	}

}
