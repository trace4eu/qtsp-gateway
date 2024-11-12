# Stage 1: Build the application
FROM maven:3.8-amazoncorretto-18 as build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
RUN mv target/*.jar app.jar

# Stage 2: Run the application
FROM openjdk:18-ea-slim-buster

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
WORKDIR /app
COPY --from=build /app/app.jar app.jar
RUN chown -R appuser:appgroup /app
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:config/application.yml"]
