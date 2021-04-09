FROM openjdk:14.0.2
ADD ./target/logs-info-back-office-0.0.1.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]