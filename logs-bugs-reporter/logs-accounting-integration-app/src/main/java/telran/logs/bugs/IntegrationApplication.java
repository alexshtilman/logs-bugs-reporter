/**
 * 
 */
package telran.logs.bugs;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import telran.logs.bugs.repo.AccountRepository;
import telran.security.accounting.mongo.documents.AccountDocument;

/**
 * @author Alex Shtilman Mar 28, 2021
 *
 */
@SpringBootApplication
@Log4j2
public class IntegrationApplication {

	@Autowired
	AccountRepository repository;

	@Value("${app-reinitialization-enabled:false}")
	boolean initializationEnabled;

	@Value("${admin-default-login:admin}")
	String adminLogin;
	@Value("${admin-default-password:admin1234.com}")
	String adminPassword;

	@Value("${developer-default-login:developer}")
	String developerLogin;
	@Value("${developer-default-password:developer1234.com}")
	String developerPassword;

	@Value("${tester-default-login:tester}")
	String testerLogin;
	@Value("${tester-default-password:tester1234.com}")
	String testerPassword;

	@Value("${assigner-default-login:assigner}")
	String assignerLogin;
	@Value("${assigner-default-password:assigner1234.com}")
	String assignerPassword;

	@Value("${teamlead-default-login:teamlead}")
	String teamleadLogin;
	@Value("${teamlead-default-password:teamlead1234.com}")
	String teamleadPassword;

	@Value("${project-owner-default-login:project-owner}")
	String projectOwnerLogin;
	@Value("${project-owner-default-password:project-owner1234.com}")
	String projectOwnerPassword;

	private long currentTimestamp = System.currentTimeMillis() / 1000;
	private long expirationTimestamp = currentTimestamp + 1000000;

	@SneakyThrows
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(IntegrationApplication.class, args);
		Thread.sleep(5000);
		context.close();
		log.debug("Initialization complete... exeting in 5 sec");
		Thread.sleep(5000);
		System.exit(1);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@PostConstruct
	void initUsers() {
		if (initializationEnabled) {
			log.debug("Strating initializating app...");
			repository.deleteAll();
			AccountDocument admin = new AccountDocument(adminLogin, passwordEncoder().encode(adminPassword),
					new String[] { "DEVELOPER", "TESTER", "ASSIGNER", "PROJECT_OWNER", "TEAM_LEAD" }, currentTimestamp,
					expirationTimestamp);
			AccountDocument developer = new AccountDocument(developerLogin, passwordEncoder().encode(developerPassword),
					new String[] { "DEVELOPER" }, currentTimestamp, expirationTimestamp);
			AccountDocument tester = new AccountDocument(testerLogin, passwordEncoder().encode(testerPassword),
					new String[] { "TESTER" }, currentTimestamp, expirationTimestamp);
			AccountDocument assigner = new AccountDocument(assignerLogin, passwordEncoder().encode(assignerPassword),
					new String[] { "ASSIGNER" }, currentTimestamp, expirationTimestamp);
			AccountDocument teamLead = new AccountDocument(teamleadLogin, passwordEncoder().encode(teamleadPassword),
					new String[] { "TEAM_LEAD" }, currentTimestamp, expirationTimestamp);
			AccountDocument projectOwner = new AccountDocument(projectOwnerLogin,
					passwordEncoder().encode(projectOwnerPassword), new String[] { "PROJECT_OWNER" }, currentTimestamp,
					expirationTimestamp);

			repository.saveAll(Arrays.asList(admin, developer, tester, assigner, teamLead, projectOwner));
		}
		log.debug("Default users created");

	}
}
