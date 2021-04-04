/**
 * 
 */
package telran.logs.bugs.service;

import static telran.security.accounting.api.Constants.ACCOUNTS_CONTROLLER;
import static telran.security.accounting.api.Constants.ACTIVATED;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.LoadBalancerComponent;
import telran.security.accounting.dto.AccountResponse;

/**
 * @author Alex Shtilman Mar 27, 2021
 *
 */
@Service
@Log4j2
public class UserDetailsRefreshService extends Thread {

	RestTemplate restTemplate = new RestTemplate();
	@Value("${app-accounting-username:user}")
	String username;
	@Value("${app-accounting-password:****}")
	String password;
	@Value("${app-refresh-timeout:30000}")
	long timeout;
	@Value("${app-accounting-provider:accounting-provider}")
	String accountingProvier;

	public UserDetailsRefreshService() {
		setDaemon(true);
	}

	@Autowired
	ConcurrentHashMap<String, UserDetails> users;

	@Autowired
	LoadBalancerComponent loadBalancer;

	@Bean
	ConcurrentHashMap<String, UserDetails> getUsersMap() {
		return new ConcurrentHashMap<>();
	}

	@Override
	@SneakyThrows
	public void run() {
		while (true) {
			System.err.println("!!!");
			fillUserDetails();
			Thread.sleep(timeout);

		}
	}

	private void fillUserDetails() {
		AccountResponse[] accounts = getAccounts();
		fillUsers(accounts);
	}

	private AccountResponse[] getAccounts() {
		AccountResponse[] accounts = {};
		try {
			String baseUrl = loadBalancer.getBaseUrl(accountingProvier);
			log.debug("accountingProvier: {}{}{}{}", baseUrl, accountingProvier, ACCOUNTS_CONTROLLER, ACTIVATED);
			ResponseEntity<AccountResponse[]> response = restTemplate
					.exchange(baseUrl + ACCOUNTS_CONTROLLER + ACTIVATED, HttpMethod.GET, null, AccountResponse[].class);
			accounts = response.getBody();
			log.debug("accounts: {}", Arrays.deepToString(accounts));
		} catch (Exception e) {
			log.debug("can not fetch accounts from lb: {}", e.getMessage());
			users.forEach((user, details) -> System.out.println(details.getUsername()));
		}
		return accounts;
	}

	private void fillUsers(AccountResponse[] accounts) {
		Arrays.stream(accounts).map(a -> new User(a.username, a.password, getAuthorities(a)))
				.forEach(ud -> users.put(ud.getUsername(), ud));

	}

	private Collection<? extends GrantedAuthority> getAuthorities(AccountResponse account) {
		String[] roles = Arrays.stream(account.roles).map(r -> "ROLE_" + r).toArray(String[]::new);
		return AuthorityUtils.createAuthorityList(roles);
	}

	private String getAuthToken() {
		String tokenText = String.format("%s:%s", username, password);
		return "Basic " + Base64.getEncoder().encodeToString(tokenText.getBytes());
	}
}
