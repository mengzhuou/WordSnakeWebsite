FROM maven:3.8.2-openjdk-17 as builder
WORKDIR /app
COPY pom.xml .
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17
VOLUME /tmp
COPY --from=builder /app/target/gtbackend-0.0.1-SNAPSHOT.jar /app.jar

ARG SPRING_DATASOURCE_URL
ARG SPRING_DATASOURCE_USERNAME
ARG SPRING_DATASOURCE_PASSWORD
ARG JWT_SECRET
ARG JWT_EXPIRATION
ARG JWT_EXPIRATIONINMS
ARG DATABASE_URL

ENV spring.datasource.url=$SPRING_DATASOURCE_URL
ENV spring.datasource.username=$SPRING_DATASOURCE_USERNAME
ENV spring.datasource.password=$SPRING_DATASOURCE_PASSWORD
ENV jwt.secret=$JWT_SECRET
ENV jwt.expiration=$JWT_EXPIRATION
ENV jwt.expirationInMs=$JWT_EXPIRATIONINMS
ENV DATABASE_URL=$DATABASE_URL

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]