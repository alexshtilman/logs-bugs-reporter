# homework 59

1. Make sure that the code doesn’t contain any println for debugging
   1. Instead of println you should use slf4j logger with appropriate method call either debug or info or warn or error
      1. debug - if there many logs for debugging purpose. For example, some function takes some objects, parameters
      1. info – if there are a few logs informing some state. For example, service has started, finished, etc.
      1. warn – if there is some state that may lead to error. For example, missing some parameter that is though optional but may be missed by error
      1. error – if there is error. For example, missing required data
1. Update project logs-provider
   1. Replace all constants in the fields of the class RandomLogs with configurable properties containing the default values. For example, @Value(“${app-count-classes:20}”) int nClasses;
1. Update project logs-db-populator
   1. Replace println with using LOG (It should be done for all projects)
   1. In the case of incorrect LogDto
      1. Using method error of the LOG, display out error message
      1. Save new LogDto object into MongoDB collection “logs”
         - Current dateTime
         - LogType.BAD_REQUEST as the log type
         - Application class name as the artifact
         - 0 as the response time
         - The error message as the result
      1. Using StreamBridge, send created in 3.2.2 LogDto object per a configurable binding name (default “exceptions-out-0”)
   1. Add tests for testing 3.2 functionality
1. Introduce, in all application projects, file application.properties
   1. File should be placed at the source directory src/test/resources
   1. File should contain required properties. At least the property for defining logging level for debugging
