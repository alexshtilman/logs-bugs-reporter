# home work 73

1. Write back-end service accounting-management with three layers (Controller-Service-Data)
   1. Package telran.security.accounting.dto (consider using Inheritance)
      1. AccountRequest comprising of the following public fields
         - Username (not empty string)
         - Password (string with minimal 8 symbols)
         - Roles (array of the strings, possible empty)
         - Expired period in minutes (positive number)
      1. AccountResponse comprising of the following public fields
         - Username
         - Password
         - Roles
         - Expiration timestamp in the seconds (number seconds from 1970-01-01)
   1. Package telran.security.accounting.api
      - ApiConstants interface with the constants for controller end point methods
   1. Package telran.security.accounting.mongo.documents
      1. AccountDoc (annotated by @Document)
         - Username as a document ID (@Id)
         - Password
         - Activation timestamp in the seconds (number seconds from 1970-01-01)
         - Roles
         - Expiration timestamp in the seconds (number seconds from 1970-01-01)
   1. Package telran.security.accounting.mongo.repo
      1. UpdateMongoOperations interface containing operations for updating accounts
      1. AccountRepository interface extending two interfaces: MongoRepository and UpdateMongoOperations
      1. UpdateMongoOperationsImpl – class implementing interface UpdateMongoOperations for implementation all updating operations using MongoTemplate class (we have done it for aggregation framework, now you should find out how to perform any Mongo update requests using MongoTemplate)
   1. Package telran.security.accounting.service
      1. AccountingManagement – interface containing of the following methods
         - AccoutResponse addAccount(AccountRequest accountDto); method that adds new account. It returns the data of the added account. The returned password should contain concatenation of {noop} and the password value. The same concatenation is saved in Mongo DB. It throws RuntimeException exception with a proper message in the case the account with the given username already exists
         - void deleteAccount(String username); removes account containing the given username. It throws RuntimeException exception with a proper message in the case the account with the given username doesn’t exist
         - AccountResponse getAccount(String username); returns account data or null if an account doesn’t exist. It doesn’t throw an exception
         - AccountResponse updatePassword(String username, String password); updates password and set new expiration timestamp (old expiration timestamp – activation timestamp + old expiration timestamp). It returns the data of the updated account. The returned password should contain concatenation of {noop} and the password value. In the cases account doesn’t exist or the new password is the same as the old one the method should throw the RuntimeException with a proper message
         - AccountResponse addRole(String username, String role); adds new role. It returns the data of the updated account. The returned password should 8 asterisks. In the cases account doesn’t exist the method should throw the RuntimeException with a proper message
         - AccountResponse removeRole(String username, String role); remove existing role. It returns the data of the updated account. The returned password should 8 asterisks. In the case either account doesn’t exist the method should throw the RuntimeException with a proper message.
      1. AccountingManagementImpl – class implementing the interface AccountingManagement
   1. Package telran.security.accounting.controllers
      1. AccountingManagementController class containing the following end point methods
         - GET request for getting account
         - POST request for adding account
         - PUT request for updating password
         - PUT request for adding new role
         - PUT request for removing existing role
         - DELETE request for removing account
   1. Package telran.security.accounting
      - AccountingmanagementAppl class containing method main and annotation @SpringBootApplication
1. Additional requirements
   1. Security
      - Introduce two users: one with role USER and other with role ADMIN
      - User with role ADMIN may do everything but user with role USER may only get account data
      - Passwords for these users should be kept in the environment variables
      - There should be possibility to disable any security for testing purposes. Consider configuration property with enabling by default. Only in the application.properties file in src/test/resourses the value should match disabling of security. Consider security configuration rule permitting all requests without any authentication/authorization
1. Unit Test
   1. Run unit test involving all three layers through the controller end-point methods as we have done in the previous projects.
   1. The security should be disabled during unit tests by setting false in the proper configuration property in the application.property file of the src/test/resources (only for testing purposes)
