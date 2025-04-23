FROM eclipse-temurin:21-jdk AS build

WORKDIR /app
COPY ./java-project/_auth-service/build/libs/_auth-service.jar ./
CMD [ "java", "-jar", "./_auth-service.jar" ]