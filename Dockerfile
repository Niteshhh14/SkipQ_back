# Multi-stage build for Spring Boot on Render
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies first
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build application
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

# Render provides PORT at runtime; keep a local default
ENV PORT=8080
ENV JAVA_OPTS=""
EXPOSE 8080

COPY --from=build /app/target/*.jar /app/app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dserver.port=${PORT} -jar /app/app.jar"]
