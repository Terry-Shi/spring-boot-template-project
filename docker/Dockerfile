# https://hub.docker.com/_/openjdk/
FROM openjdk:8-jre-alpine


ADD build/libs/spring-boot-template-project-1.0.0.jar  /

ENTRYPOINT ["java", "-jar", "/spring-boot-template-project-1.0.0.jar"]
#CMD ["java", "-jar", "/spring-boot-template-project-1.0.0.jar"]