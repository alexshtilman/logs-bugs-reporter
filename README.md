# home work 71

1. Fix existing Dockerfile files for the projects of the classwork #71
   - replace openjdk:14.0.1 with openjdk:14.0.2 (14.0.1 has bug with TLS-1.3 authentication we have faced at classwork #71 working with Mongo on the Atlas clouding)
1. Fix project logs-bugs-email-notifier
   1. In the application.properties replace localhost with the proper service names that exist in the docker-compose.yml file
      - `app-url-assigner-mail=http://assigner-email-provider:9292`
      - `app-url-programmer-mail=http://programmer-email-provider:9393`
1. Update projects that apply PostgreSQL
   - application.properties files should contain configuration matching PostgreSQL on Clouding
   - application.properties files shouldn’t contain sensitive data such as Database password. Apply environment variables instead
1. Update projects that apply MongoDB
   - Application.properties files should contain configuration matching MongoDB on Clouding (spring.data.mongodb.uri)
   - application.properties files shouldn’t contain sensitive data such as Database password. Apply environment variables instead
   - The issue with DB authentication is related to the bug in several JDK versions. For Docker you should use openjdk:14.0.2. If you have issue in your IDE you should use additional JVM argument -Djdk.tls.client.protocols=TLSv1.2 as I’ve sent photo in Skype group.
1. Move functionality of creating artifacts and programmers tables for Integration Test from the project logs-bugs-reporter-back-office to the project logs-provider, as the project logs-provider is intended only for Integration Test, while the project logs-bugs-reporter-back-office should be the essential one in a produc
1. Create the proper Dockerfile files for each application project
1. Update docker-compose file from the classwork #71
   1. Add all required services except log-provider that will be ran for Integration Test only. Make sure that all back-office projects expose out their ports allowing the users to work from a host machine while other services shouldn’t do it
   1. Add environment parameters for all services using environment variables of sensitive data (passwords)
      - If you use 2-step verification for Google Mail, you should generate new application password, specifying custom device as “Mail on Docker”
1. Make sure the pom.xml files of each application project contain build element that we have taken out from the parent project at the classwork #71
1. For parent project, create Run Configuration for launching Maven installation containing two build goals “clean” and “install”. Check the check-box for skipping unit tests (at this step building with all unit tests will take a lot of time)
1. Run maven build and make sure all projects have been built successfully
1. Make sure no container is running in the Docker. You may use your Docker desktop application for removing all containers.
1. Run command docker-compose build . Current path on Terminal should match the placeholder of the docker-compose.yml file
1. Run command docker-compose up –d for running all services from docker-compose.yml
   - Make sure all services have started
1. Integration Test scenario
   1. From your IDE, launch the logs-provider application that should send to Kafka the logs and create three tables: artifacts containing the artifacts data, programmers containing the programmers data and bugs containing nothing
   1. Look at logs and once there are several exceptions including that with non-existing artifact, stop the project logs provider
   1. Check email box that should contain the proper email messages
   1. From postman, run several requests for testing logs-info-back-office
      - `http://localhost:<port exposed out for logs-info-back-office service>/<end-point API>`
   1. From postman, run several requests for testing logs-bugs-reporter-back-office
      - `http://localhost:<port exposed out for logs-bugs-reporter-back-office service>/<end-point API>`
