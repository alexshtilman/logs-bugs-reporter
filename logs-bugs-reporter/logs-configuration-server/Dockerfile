FROM openjdk:14.0.1
ADD ./target/logs-configuration-server-0.0.1.jar app.jar
EXPOSE 8282
ENTRYPOINT ["java","-jar","app.jar"]