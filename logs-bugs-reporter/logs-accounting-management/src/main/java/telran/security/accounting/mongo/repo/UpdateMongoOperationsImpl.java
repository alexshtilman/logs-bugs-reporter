/**
 * 
 */
package telran.security.accounting.mongo.repo;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import lombok.extern.log4j.Log4j2;
import telran.security.accounting.mongo.documents.AccountDoc;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
@Log4j2
public class UpdateMongoOperationsImpl implements UpdateMongoOperations {

	private final MongoTemplate mongoTemplate;

	@Autowired
	public UpdateMongoOperationsImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public AccountDoc updatePasswordByUserName(String username, String password) {

		AccountDoc user = mongoTemplate.findOne(Query.query(Criteria.where("username").is(username)), AccountDoc.class);
		if (user != null) {
			user.setPassword(password);
			mongoTemplate.save(user);
		}

		return user;
	}

	@Override
	public AccountDoc addRoleByUserName(String username, String role) {
		AccountDoc user = mongoTemplate.findOne(Query.query(Criteria.where("username").is(username)), AccountDoc.class);

		if (user != null) {
			Set<String> roles = new LinkedHashSet<String>(Arrays.asList(user.getRoles()));
			roles.add(role);
			user.setRoles(roles.toArray(new String[0]));
			mongoTemplate.save(user, "accounts");
		}

		return user;
	}

	@Override
	public AccountDoc removeRoleByUserName(String username, String role) {
		AccountDoc user = mongoTemplate.findOne(Query.query(Criteria.where("username").is(username)), AccountDoc.class);
		if (user != null) {
			Set<String> roles = new LinkedHashSet<String>(Arrays.asList(user.getRoles()));
			roles.remove(role);
			user.setRoles(roles.toArray(new String[0]));
			mongoTemplate.save(user, "accounts");
		}
		return user;
	}

}
