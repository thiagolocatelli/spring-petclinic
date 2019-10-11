FROM java:8-jre-alpine

EXPOSE 8080

RUN mkdir /app
COPY target/spring-petclinic.jar /app/spring-boot-application.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]
