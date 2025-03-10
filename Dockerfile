FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY java-project/gradlew ./
COPY java-project/gradlew.bat ./
COPY java-project/gradle/ ./gradle
COPY java-project/settings.gradle.kts ./

COPY java-project/app/ ./app

RUN ./gradlew build
CMD [ "java", "-jar", "./app/build/libs/app.jar" ]