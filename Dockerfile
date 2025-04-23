FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY java-project/gradlew ./
COPY java-project/gradlew.bat ./
COPY java-project/gradle/ ./gradle
COPY java-project/settings.gradle.kts ./

COPY java-project/_auth-service/ ./_auth-service
COPY java-project/_room-service/ ./_room-service

RUN ./gradlew build

CMD [ "java", "-jar", "./app/build/libs/app.jar" ]