# homework 57
1. Create new module logs-provider inside logs-bugs-reporter parent project
1. Add dependency for LogDto inside pom.xml
1. Write class RandomLogs with the following content
   1. Annotation @Component for getting an appropriate bean inside an application context
   1. Method createRandomLog() that returns random log according to the following
      - dateTime – new Date();
      - logType : Probability of exception is 10% (should be field exceptionProb); Among all exceptions the probability of security exceptions is 30% (should be field secExceptionProb); Among all security exceptions the probability of authentication exception is 70% ; Non-security exceptions are generated with the equaled probabilities
      - artifact: if the type is NO_EXCEPTION, or BAD_REQUEST_EXCEPTION, or NOT_FOUND_EXCEPTION, or DUPLICATED_KEY_EXCEPTION an artifact contains concatenation “class” word and random number in range ; if the type is AUTHENTICATION_EXCEPTION an artifact contains “authentication” ; if the type is AUTHORIZATION_EXCEPTION an artifact contains “authorization”
      - responseTime: if the type is NO_EXCEPTION – number greater than 0 for other cases 0
      - result: empty string
      - ![image](https://github.com/alexshtilman/logs-bugs-reporter/blob/homework/picture.png?raw=true)
   1. Write and run Unit test (think of the annotations for creating a test application contexts with the bean of the class RandomLogs)
      1. One test method performing the following
         - Creates 100000 random logs
         - Displays out number logs for each log type (remember Java streams)
         - Checks rules described in 3.2.3-3.2.5 sections 
