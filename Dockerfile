FROM openjdk:8-jdk-alpine
LABEL author="spartan" email=""
VOLUME /tmp
ARG JAR_FILE 
COPY target/qbthon-email-service-0.0.1-SNAPSHOT.jar qbthon-email-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/qbthon-email-service-0.0.1-SNAPSHOT.jar"]