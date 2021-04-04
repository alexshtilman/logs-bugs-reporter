# home work 75

1. Write new micro-service logs-bugs-accounts-provider
   1. The service should work with the MongoDB Database containing the accounts from AccountingManagement application we have developed on the previous lessons. (AccountingManagement is a standalone application that is not included in the micro-services landscape.
   1. The service performs only one action that is retrieving all non-expired accounts from Database
   1. The service should register itself on the Eureka discovery server. That will allow using a client side load balancer
   1. By mapping a REST GET request an end-point method should return a list of all activated accounts
   1. Write JUnit test for testing the activated accounts provisioning
1. Completing logs-bugs-gateway service
   1. The service should be a part of the Microservices landscape
   1. The service should have two configurations for two profiles dev and docker
      - Configuration for dev implies using of the localhost
      - Configuration for docker implies using the proper names of the micro-services network
   1. The service should run proxy functionality to logs-bugs-reporter-back-office and logs-info-back-office services
   1. The service should involve Spring security with the following configurations (Note: Security relates only access from outside the micro-services landscape. The access and any actions inside the micro-services landscape are not secured. The landscape has single point of outside access in the gateway)
      1. All accounts should be retrieved from the logs-bugs-accounts-provider
      - The accounts data in the logs-bugs-gateway service should be retrieved periodically in the configurable interval
      1. Access to the information about all logs should be permitted only for role DEVELOPER
      1. Opening bugs, as with assignment as well as without, should be permitted for the roles TESTER, ASSIGNER, and DEVELOPER
      1. Assignment after opening bugs should be permitted only for role ASSIGNER
      1. Closing bugs should be permitted only for role TESTER
      1. Adding programmer should be permitted only for role PROJECT_OWNER
      1. Adding artifact should be permitted for roles TEAM_LEAD and ASSIGNER
      1. Getting any information about bugs should be permitted for any authenticated user
   1. Write Junit test only for security (Note: the proxy functionality is tested only in the integration tests)
      1. For testing authorization, you may use @WithMockUser annotation
      1. For testing authentication, you should mock the UserDetailsRefreshService bean. It will be some challenge for you.
1. Docker Solution
   1. Add Docker containers data for logs-bugs-gateway and logs-bugs-accounts-provider
   1. Only container for logs-bugs-gateway should open access from outside. So all ports data should be taken out from other services
1. Integration Test
   1. Launch AccountingManagement application
   1. Using postman, add several accounts with different roles
   1. Start docker-compose and make sure that all services have started
   1. Launch logs-provider application
   1. Using postman, run several requests to both logs-bugs-reporter-back-office (bugs-reporter-back service) and logs-info-back-office (logs-back service) applications
