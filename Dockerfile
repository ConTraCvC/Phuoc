# syntax=docker/dockerfile:1
FROM openjdk:11
WORKDIR D:/Docker
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN mvn dependency:go-offline
COPY src ./src
CMD ["mvn", "spring-boot:run"]