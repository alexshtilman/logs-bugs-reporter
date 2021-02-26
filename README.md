# home work 67

1. Complete the logs-bugs-reporter-back-office application with all three layers (controller-services-data) and tests. Think of the proper REST requests (POST, PUT, GET) for the controller end-point methods
1. Add three layers with the tests for the following functionality
   - Getting distribution of the bug counts by bugs seriousness `List<SeriousnessBugCount> getSeriousnessBugCounts()`
   - Getting a given number of the seriousness types with the most bugs `List<Seriousness> getSeriousnessTypesWithMostBugs(int nTypes)`

Implementation notes:

- Apply the proper REST requests (POST, PUT, GET)
- Apply repository naming convention wherever possible
- Apply JPQL in the cases when naming is impossible
- Apply SQL native in the cases when naming and JPQL is impossible
