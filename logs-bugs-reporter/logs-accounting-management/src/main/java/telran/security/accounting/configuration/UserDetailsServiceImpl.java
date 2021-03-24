package telran.security.accounting.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import telran.security.accounting.mongo.documents.AccountDocument;
import telran.security.accounting.repo.AccountRepository;

@Service
@Log4j2
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private AccountRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		AccountDocument userData = userRepository.getByUsername(username);
		if (userData == null) {
			throw new UsernameNotFoundException(username);
		}
		log.debug("data: {}", userData);
		return new User(userData.getUsername(), 
				userData.getPassword(), // already encoded !!!
				AuthorityUtils.createAuthorityList(userData.getRoles()));
	}

}
