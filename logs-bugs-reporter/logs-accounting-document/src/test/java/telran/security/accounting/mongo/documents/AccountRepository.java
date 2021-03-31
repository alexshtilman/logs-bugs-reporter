/**
 * 
 */
package telran.security.accounting.mongo.documents;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author Alex Shtilman Mar 28, 2021
 *
 */
public interface AccountRepository extends ReactiveMongoRepository<AccountDocument, String> {

}
