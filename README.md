# home work 70

1. Sanity test of all application
   1. Preconditions and test scenario are the same as in the previous HW with making sure the test recipient account doesn’t contain any mails from the application
   1. Additionally, to create and configure Gmail account, from which there will be sent mail notifications
      - Set up 2-Step verification (Application – Gmail (your account) –Recipient Mail sever account)
      - Generate application password that should appear in a configuration as a value of the environment variable MAIL-PASSWORD
      - Add required configuration for the service logs-bugs-email-provider
   1. Launch logs-provider application and stop it in couple of minutes
   1. Launch logs-bugs-reporter-back-office and make sure the table’s programmers and artifacts contain relevant for the integration test data. The table bugs should be empty
   1. Launch logs-bugs-email-provider application. Make sure the application has started on the proper configured port
   1. Launch logs-bugs-assigner-mail-provider application. Make sure the application has started on the proper configured port
   1. Launch all rest applications in any order
   1. Make sure everything works according to the sanity test of previous HW and the test recipient account contains the proper mail messages
