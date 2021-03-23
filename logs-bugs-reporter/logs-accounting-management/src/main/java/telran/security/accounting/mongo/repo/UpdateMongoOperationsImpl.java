/**
 * 
 */
package telran.security.accounting.mongo.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import telran.security.accounting.mongo.documents.AccountDoc;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */

public class UpdateMongoOperationsImpl implements UpdateMongoOperations {

	private final MongoTemplate mongoTemplate;

	@Autowired
	public UpdateMongoOperationsImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public AccountDoc updatePasswordByUserName(String username, String password, long newActivation,
			long newExpiration) {
		Query query = new Query();
		query.addCriteria(Criteria.where("username").is(username));
		Update update = new Update();
		update.set("password", password);
		update.set("activationTimestamp", newActivation);
		update.set("expirationTimestamp", newExpiration);
		return mongoTemplate.findAndModify(query, update, AccountDoc.class);

	}


	@Override
	public AccountDoc addRoleByUserName(String username, String role) {
		return updateRoles(username, role, true);
	}

	private AccountDoc updateRoles(String username, String role, boolean flAdd) {
		Query query = new Query();

		query.addCriteria(Criteria.where("username").is(username));
		Update update = new Update();
		if (flAdd) {
			update.push("roles", role);
		} else {
			update.pull("roles", role);
		}

		return mongoTemplate.findAndModify(query, update, AccountDoc.class);
	}

	@Override
	public AccountDoc removeRoleByUserName(String username, String role) {
		return updateRoles(username, role, false);
	}
}
