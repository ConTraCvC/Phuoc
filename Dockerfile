FROM openjdk:11
EXPOSE 8082
ADD target/spring-boot-docker.jar
ENTRYPOINT ["java", "-jar","spring-boot-docker.jar"]
WORKDIR D:/Docker
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY .src ./src
CMD ["./mvnm", "spring-boot:RUN"]