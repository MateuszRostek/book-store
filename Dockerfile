FROM maven:3.9.6-eclipse-temurin-17 as builder
WORKDIR application
COPY src pom.xml checkstyle.xml ./
RUN mvn clean package

FROM openjdk:17-jdk-slim
WORKDIR application
COPY --from=builder application/target/application.jar ./
ENTRYPOINT ["java", "application.jar"]
