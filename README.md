# home work 71

## Test urls

```text
http://10.0.0.10:8282/karafka/dev
http://10.0.0.10:9292/mail/get_assigner_mail
http://10.0.0.10:9393/mail/class%2010
http://10.0.0.10:8080/bugs/seriosness_bugs_count
http://10.0.0.10:8081/statistics/artifact_and_count
```

## DOCKER SWARM

`docker swarm init --advertise-addr 10.0.0.10` - init manager of swarm on ip 10.0.0.10
`docker swarm leave --force` - leave swarm

## how to fix ingress issues

origin: `https://stackoverflow.com/questions/59007780/container-running-on-docker-swarm-not-accessible-from-outside`

```bash
docker network create \
 --driver overlay \
 --ingress \
 --subnet 172.16.0.0/16 \
 --gateway 172.16.0.1 \
 ingress
```

## run portainer

`docker run -d -p 8000:8000 -p 9000:9000 --name=portainer --restart=always -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer-ce`

## workaround env variables use of docker-compose instead of secrets

`export $(cat .env) > /dev/null 2>&1; docker stack deploy --compose-file docker-compose.yml ${1:-logs-bugs-reporter}`

## save docker images to personal account at dockerhub

```bash
docker tag logs-bugs-reporter_populator gungam/logs-bugs-reporter_populator
docker tag logs-bugs-reporter_opening gungam/logs-bugs-reporter_opening
docker tag logs-bugs-reporter_assigner-email-notifier gungam/logs-bugs-reporter_assigner-email-notifier
docker tag logs-bugs-reporter_email-provider gungam/logs-bugs-reporter_email-provider
docker tag logs-bugs-reporter_info-back-office gungam/logs-bugs-reporter_info-back-office
docker tag logs-bugs-reporter_reporter-back-office gungam/logs-bugs-reporter_reporter-back-office
docker tag logs-bugs-reporter_logs-analyzer gungam/logs-bugs-reporter_logs-analyzer
docker tag logs-bugs-reporter_assigner-email-provider gungam/logs-bugs-reporter_assigner-email-provider
docker tag logs-bugs-reporter_config-server gungam/logs-bugs-reporter_config-server
```

```bash
docker push gungam/logs-bugs-reporter_populator
docker push gungam/logs-bugs-reporter_opening
docker push gungam/logs-bugs-reporter_assigner-email-notifier
docker push gungam/logs-bugs-reporter_email-provider
docker push gungam/logs-bugs-reporter_info-back-office
docker push gungam/logs-bugs-reporter_reporter-back-office
docker push gungam/logs-bugs-reporter_logs-analyzer
docker push gungam/logs-bugs-reporter_assigner-email-provider
docker push gungam/logs-bugs-reporter_config-server
```

## Homework description

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
