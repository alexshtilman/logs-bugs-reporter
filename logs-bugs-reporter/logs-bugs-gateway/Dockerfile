FROM openjdk:14.0.2
ADD ./target/logs-bugs-gateway-0.0.1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]