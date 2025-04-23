FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn package

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/*.jar prod.jar
ENTRYPOINT ["java", "-jar", "/app/prod.jar"]