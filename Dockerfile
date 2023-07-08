FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar spring-boot-security-jwt.jar
ENTRYPOINT ["java","-jar","/spring-boot-security-jwt.jar"]
EXPOSE 8080