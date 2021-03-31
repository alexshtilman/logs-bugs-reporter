/**
 * 
 */
package telran.security.accounting.mongo.documents;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import telran.security.accounting.dto.AccountRequest;

/**
 * @author Alex Shtilman Mar 31, 2021
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AccountRepository.class)
@EnableAutoConfiguration
@AutoConfigureDataMongo
class AccountingDocumentTest {

	@Autowired
	AccountRepository accounts;

	@Test
	void docStoreTest() {

		AccountRequest account = new AccountRequest("user", "{noop} password", new String[] { "USER", "READ" }, 999);

		accounts.save(new AccountDocument(account)).block();
		AccountDocument expected = accounts.findAll().blockFirst();
		assertNotNull(expected.getUsername());
		assertEquals(account.password, expected.getPassword());
		assertArrayEquals(account.roles, expected.getRoles());
	}
}
