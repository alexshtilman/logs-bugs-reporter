package telran.security.accounting.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.security.accounting.mongo.documents.AccountDocument;

public interface AccountRepository extends MongoRepository<AccountDocument, String>, UpdateMongoOperations {

	List<AccountDocument> findByExpirationTimestampGreaterThan(long l);

}
