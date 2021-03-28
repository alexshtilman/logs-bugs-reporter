/**
 * 
 */
package telran.logs.bugs.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.security.accounting.mongo.documents.AccountDocument;

/**
 * @author Alex Shtilman Mar 28, 2021
 *
 */
public interface AccountRepository extends MongoRepository<AccountDocument, String> {

}
