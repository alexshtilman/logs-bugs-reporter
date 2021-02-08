# home work 62

1. Complete logs-bugs-email-notifier

   1. There should be additional method getAssignerMail of the class EmailProviderClient
      - So far this method return null with //TODO communicating with sync service for getting assigner’s email
   1. If the method getEmailByArtifact returns the empty string, the method getAssignerMail call should be performed for getting assigner email
      - Assumed that the method getAssignerMail should return any mail. If not the service will log error (LOG.error(“email ‘to’ has received neither from logs-bugs-email-provider nor from logs-bugs-assigner-mail-provider”)
   1. Subject should be configured property with the default value “exception”
   1. Text should be the following

      - For programmer as follows

        ```txt
        Hello, Programmer
        Exception has been received
        Date: <date>
        Exception type: <Exception type>
        Artifact: <artifact>
        Explanation: <result from a LogDto object>
        ```

      - For assigner as follows

        ```txt
        Hello, Opened Bugs Assigner
        Exception has been received
        Date: <date>
        Exception type: <Exception type>
        Artifact: <artifact>
        Explanation: <result from a LogDto object>
        ```

      - Write tests according to the above functionality

1. Write new sync service logs-bugs-assigner-mail-provider with RestController
   1. End-point (GET request) returns the assigner email as being configured application property
   1. Write test for the end-point
