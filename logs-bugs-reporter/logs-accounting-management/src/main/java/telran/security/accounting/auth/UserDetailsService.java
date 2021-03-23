/**
 * 
 */
package telran.security.accounting.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import telran.security.accounting.mongo.documents.AccountDoc;
import telran.security.accounting.mongo.repo.AccountRepository;

/**
 * @author Alex Shtilman Mar 23, 2021
 *
 */
@Component
public class UserDetailsService implements ReactiveUserDetailsService {

	@Autowired
	AccountRepository accountRepo;

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		Mono<AccountDoc> data = accountRepo.findByUsername(username);
		return data.cast(UserDetails.class);
	}

}
