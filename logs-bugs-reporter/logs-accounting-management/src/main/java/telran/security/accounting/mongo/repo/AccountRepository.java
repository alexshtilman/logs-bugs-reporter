/**
 * 
 */
package telran.security.accounting.mongo.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import telran.security.accounting.mongo.documents.AccountDoc;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
public interface AccountRepository extends MongoRepository<AccountDoc, ObjectId>, UpdateMongoOperations {

	AccountDoc findByUsername(String username);

	Long deleteByUsername(String username);
}
