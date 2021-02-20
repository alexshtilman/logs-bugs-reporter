# home work 65

1. Write implementations of the following LogsInfo interface method
   1. Getting “nExceptions” most encountered Exception Types `Flux<LogType> getMostEncounteredExceptionTypes(int nExceptions)`
   1. Getting information about each artifact and counts of occurrences `Flux<ArtifactCount> getArtifactOccurrences();`
   1. Getting “nArtifacts” most encountered artifacts `Flux<String> getMostEncounteredArtifacts(int nArtifacts);`
2. Write the appropriate REST controller end-point methods for serving the above methods based on Webflux
   Note: that for method of # 1.3 the end-point method should return `Mono<List<String>` rather than `Flux<String>`. It’s related to the following extract from the Spring documentation:

   ```text
   both Jackson2Encoder and Jackson2Decoder do not support elements of type String. Instead the default assumption is that a string or a sequence of strings represent serialized JSON content, to be rendered by the CharSequenceEncoder. If what you need is to render a JSON array from Flux<String>, use Flux#collectToList() and encode a Mono<List<String>>”. This extract is taken from the documentation https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html It means that the method collectList() of the Flux<String> will return Mono<List<String>>
   ```

3. Write the tests based on the following webflux test structure

   ```java
   webClient.get().uri(<url string>).exchange().expectStatus()
   .isOk().expectBody(T[].class)
   .isEqualTo(<expected array>)
   ```

   For example, you prepare Database such that most encountered two artifacts are “artifact1” and “artifact2”. URI for getting most encountered artifacts is /logs/artifacts/encountered?amount=2. You define array String expectedArtifacts[] = {“artifact1”, “artifact2”} and the test will be as follows

   ```java
   webClient.get().uri(“/logs/artifacts/encountered?amount=2”).exchange().expectStatus().isOk().expectBody(String[].class).isEqualTo(expectedArtifacts);
   ```

   Try to avoid a repeated code (DRY) by considering additional parameterized test method (We have applied the parameterized methods it in the Java core)
