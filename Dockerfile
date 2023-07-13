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

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]