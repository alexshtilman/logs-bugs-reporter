# homework 60

1. Write service logs-bugs-openning (commit CW-60 contains only primitive tests and annotations that should be used in the application and test classes)
   1. The service should be subscribed (Placing functional interface Consumer<LogDto> in an application context, see previous projects) for taking exception logs
   1. The service should create bug according to the exception log:
      1. Opening date should be the current date
      1. Closing date should be null
      1. Seriousness
         - Blocking for Authentication exception
         - Critical for Authorization and Server exceptions
         - Minor for others
      1. Get programmer from an appropriate repository according to the artifact value from the exception
         - In the case the programmer related to the artifact exists the bug should be in the state ASSIGNED with reference to the programmer
         - In the case the programmer related to the artifact doesn’t exist the bug should be in the state OPENNED (it should be followed by logging warning). In this case the reference to a programmer should be null
      1. OpenningMode should be set as AUTOMATIC
      1. Description should contain text as the concatenation exception type and result
   1. The service should contain the debug and warning logs according to common sense
1. Write tests testing the service logs-bugs-openning
   1. Tests of the CW-60 commit only for specifying annotations. You should write tests similar to the ones of the project students. You should use SQL script for testing data of programmers and artifacts
