package telran.security.accounting.service;

import java.util.List;

import telran.security.accounting.dto.*;

public interface AccountingManagement {
AccountResponse addAccount(AccountRequest accountDto); 
void deleteAccount(String username);
AccountResponse getAccount(String username); 
AccountResponse updatePassword(String username, String password); 
AccountResponse addRole(String username, String role); 
AccountResponse removeRole(String username, String role); 
List<AccountResponse> getActivatedAccounts();

}
