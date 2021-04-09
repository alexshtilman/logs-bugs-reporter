FROM openjdk:14.0.2
ADD ./target/logs-bugs-email-notifier-0.0.1.jar app.jar
EXPOSE 9191
ENTRYPOINT ["java","-jar","app.jar"]