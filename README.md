# homework 58

1. Microservice logs-provider
   1. Classwork contains test with printing out the log content. Your task is to write automation test that checks that there have been 10 logs and all logs are different (there is no two equaled logs)
1. Microservice logs-db-provider

   1. Complete functionality of the method receiving from a producer log object and saving it into MongoDB - Validation:
      The error of the classwork was in that the Spring Cloud should had inserted a real implementation of the interface Validator into an Application Context. However, an implementation had not been specified in the pom.xml file. The dependency resolves the issue

      ```xml
      <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>6.0.13.Final</version>
      </dependency>
      ```

      To get reference on the implementation of the javax.validation.validator interface you should use @Autowired annotation. The validator has method validate that gets reference to LogDto object and returns Set of ConstraintViolation objects. If the set is not empty, the validation errors exist. Each object has method getMessage(). So far, you should validate LogDto object, display out all error messages.

      - Storing in MongoDB
        You should apply the same functionality with Mongo repository that we have applied in the project logs-bugs-mongo-document for testing. Only in the project logs-db-provider it should be a part of the functionality rather than testing from the project logs-bugs-mongo-document
      - Testing
        You should write test cases for testing saving documents in the case of the valid logs and not saving documents in the case of the invalid logs
