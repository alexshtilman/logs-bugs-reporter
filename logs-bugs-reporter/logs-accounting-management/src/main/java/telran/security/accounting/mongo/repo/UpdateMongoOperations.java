/**
 * 
 */
package telran.security.accounting.mongo.repo;

import telran.security.accounting.mongo.documents.AccountDoc;

/**
 * @author Alex Shtilman Mar 19, 2021
 *
 */
public interface UpdateMongoOperations {

	AccountDoc updatePasswordByUserName(String username, String password, long newActivation, long newExpiration);

	AccountDoc addRoleByUserName(String username, String role);

	AccountDoc removeRoleByUserName(String username, String role);
}
