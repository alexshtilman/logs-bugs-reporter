FROM openjdk:14.0.1
ADD ./target/logs-bugs-email-provider-0.0.1.jar app.jar
EXPOSE 8877
ENTRYPOINT ["java","-jar","app.jar"]