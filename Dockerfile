FROM openjdk:17
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY ./Backend/target/*.jar /Backend/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/Backend/app.jar"]