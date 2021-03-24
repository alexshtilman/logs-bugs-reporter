package telran.security.accounting.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import telran.security.accounting.dto.AccountResponse;
import telran.security.accounting.mongo.documents.AccountDocument;


public class UpdateMongoOperationsImpl implements UpdateMongoOperations {
	@Autowired
	MongoTemplate mongoTemplate;


	@Override
	public AccountDocument updatePassword(String username, String newPassword, long newActivation, long newExpiration) {
		Query query = new Query();

		query.addCriteria(Criteria.where("username").is(username));
		Update update = new Update();
		update.set("password", newPassword);
		update.set("activationTimestamp", newActivation);
		update.set("expirationTimestamp", newExpiration);
		return mongoTemplate.findAndModify(query, update, AccountDocument.class);

	}

	@Override
	public AccountDocument addRole(String username, String role) {
		return updateRoles(username, role, true);
	}

	private AccountDocument updateRoles(String username, String role, boolean flAdd) {
		Query query = new Query();

		query.addCriteria(Criteria.where("username").is(username));
		Update update = new Update();
		if(flAdd) {
			update.push("roles", role);
		} else {
			update.pull("roles", role);
		}
		
		return mongoTemplate.findAndModify(query, update, AccountDocument.class);
	}

	@Override
	public AccountDocument removeRole(String username, String role) {
		return updateRoles(username, role, false);
	}

	@Override
	public List<AccountResponse> getActivatedAccounts() {
		// TODO Auto-generated method stub
		return null;
	}

}
