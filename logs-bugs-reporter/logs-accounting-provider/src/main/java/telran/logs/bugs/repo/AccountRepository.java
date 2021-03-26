/**
 * 
 */
package telran.logs.bugs.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.security.accounting.mongo.documents.AccountDocument;

/**
 * @author Alex Shtilman Mar 26, 2021
 *
 */
public interface AccountRepository extends MongoRepository<AccountDocument, String> {

	List<AccountDocument> findByExpirationTimestampGreaterThan(long l);

}
