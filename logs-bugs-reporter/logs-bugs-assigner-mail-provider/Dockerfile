FROM openjdk:14.0.1
ADD ./target/logs-bugs-assigner-mail-provider-0.0.1.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","app.jar"]