FROM eclipse-temurin:21-jdk AS build

WORKDIR /app
COPY ./java-project/_room-service/build/libs/_room-service.jar ./
CMD [ "java", "-jar", "./_room-service.jar" ]