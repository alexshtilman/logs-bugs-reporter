package telran.security.accounting.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.security.accounting.mongo.documents.AccountDocument;

public interface AccountRepository extends MongoRepository<AccountDocument, String>, UpdateMongoOperations {

	AccountDocument getByUsername(String username);

}
