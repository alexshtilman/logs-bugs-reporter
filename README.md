# home work 66

1. Complete the flow of controller-service-data for the methods we have done at the service-data layers
   - `addProgrammer` POST request `/bugs/programmers`. Gets `ProgrammerDto` object with a relevant validation (consider annotation `@Valid`) and returns the same `ProgrammerDto` object in the case of success (exceptions will be considered later)
   - `openBug`. POST request `/bugs/open` . Gets BugDto object with a relevant validation (consider annotation `@Valid`) and returns the BugResponseDto object in the case of success (exceptions will be considered later)
   - `openAndAssignBug`. POST request `/bugs/open/assign` . Gets BugAssignDto object with a relevant validation (consider annotation `@Valid`) and returns the `BugResponseDto` object in the case of success (exceptions will be considered later)
   - `assignBug`. PUT request `/bugs/assign`. Gets AssignData object with a relevant validation (consider annotation @Valid) and returns nothing in the case of success (exceptions will be considered later)
   - `getBugsProgrammer` GET request `/bugs/programmers` Gets parameter programmer_id and returns `List<BugResponseDto>` containing objects related to the bugs having been assigned to a programmer with the given id
1. Write Unit tests for these methods

   - For POST requests you may use the following template:

     ```java
     testClient.post().uri(<uri string>).contentType(MediaType.APPLICATION_JSON)
     .bodyValue(<object passed in request>).exchange().expectStatus(). <ok or bad request>().

     <only for valid requests>body(<class object for the returned type>. isEqualTo(<expected object>)
     ```

   - For PUT request of the method `assignBug` you may use the same template as for POST requests just instead of post there will be put and the method returns nothing
   - For GET request of the method `getBugsProgrammer` you may use the following template:

   ```java
   testClient.get().uri(<uri string with parameter> ).exchange().expectStatus().<ok or bad request> ()
   .expectBodyList(<element class object>).isEqualTo(<list of expected objects>);
   ```
