# home work 63

1. So far, commit of CW-63 contains the following issues:
   1. compilation error in logs-bugs-mongo-document project: the method findAll returns `Flux<LogDoc>` rather than `List<logDto>`;
   1. bug in logs-db-populator as Spring Data Mongodb Reactive implies ReactiveMongoRepository as the base repository
      You should to fix all the issues by using Flux/Mono functionality
