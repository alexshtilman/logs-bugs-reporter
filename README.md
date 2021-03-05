# home work 69

1. Add application.properties files with the proper spring configuration values (required configuration for PostgreSQL on the local machine may be found in the previous project “Students”)

1. Sanity Integration test

   1. Preconditions
      1. PostgreSQL Database programmers_artifacts_bugs is created on the local machine and contains the following:
         - Table “programmers” contain 17 records (id – any unique value, name – any meaningful name `(David, Moshe,….), email - <real Gmail account> + <id>@gmail.com` , for example, logs.bugs.reporter+123@gmail.com (try to create them from @PostConstruct method in the service logs-bugs-reporter-back-office with the proper configuration)
         - Table “artifacts” contains 22 artifact records (try to create them from @PostConstruct method in the service logs-bugs-reporter-back-office with the proper configuration). The 22 artifacts should match the artifacts from the method getArtifact() of the project logs-bugs-random (class RandomLogs). It means the artifactId’s values: “authentication”, “authorization”, “class1” …”class20”. Thus 22 artifacts with random programmers from 2.1.1.1 (Remember one programmer may be responsible for several artifacts, and generally the situation that programmer doesn’t have any artifact is possible as well)
         - Table “bugs” – empty
      1. MongoDb on the local machine. Collection “logs” in the DB may be empty or may not exist at all
   1. Test case scenario
   1. Make sure all services are started and console logs contain the proper log messages
   1. After several minutes, stop service logs-provider
   1. Make sure MongoDB contains the number logs, proportional of the log-provider running time (one log per one second)
   1. Make sure logs-info-back-office works properly (run several requests from either Browser or Postman). The number of NO_EXCEPTION should be approximately 90%
   1. Make sure the number of exceptions (from logs-info-back-office) matches the number of bugs (from logs-bugs-reporter-back-office)
   1. Make sure the logs-bugs-reporter-back-office works properly (Run several requests using Postman)

1. your should run microservices with the proper order:
   - logs-bugs-reporter-back-office ( it drops and creates postgress data base and fill it with artifacts and programmers from application.properties)
   - logs-info-back-office
   - logs-analyzer ( will get from topic default and send to topic exceptions and logs-validated)
   - logs-db-populator ( will get from topic logs-validated and save to mongodb database)
   - logs-bugs-opening ( will get from topic exceptions and save to postgress database)
   - logs-provider ( will send random logs to topic default)
1. My back office has 2 API:
   - logs-info-back-office: `http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/`
   - logs-bugs-reporter-back-office:`http://localhost:8081/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/`
