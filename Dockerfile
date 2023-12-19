FROM maven:eclipse-temurin AS builder

WORKDIR /src

COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY src src
COPY pom.xml .
COPY email email

RUN mvn package -Dmaven.test.skip=true

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /src/target/project-ss-0.0.1-SNAPSHOT.jar app.jar
COPY --from=builder /src/email email

ENV SPRING_REDIS_HOST=localhost SPRING_REDIS_PORT=6379
ENV SPRING_REDIS_USERNAME=default SPRING_REDIS_PASSWORD=password
ENV RESEND_EMAIL_APIKEY=abc123 LAZADA_DATAHUB_APIKEY=abc123
ENV PORT=8080 WEBAPP_HOST_URL=url

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar
