FROM openjdk:14.0.2
ADD ./target/logs-discovery-server-0.0.1.jar app.jar
EXPOSE 8283
ENTRYPOINT ["java","-jar","app.jar"]