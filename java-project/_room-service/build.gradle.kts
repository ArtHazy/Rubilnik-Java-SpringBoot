plugins {
    application
    id("org.springframework.boot") version "3.3.2"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.guava)

    implementation(project(":__core"))

    implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-websocket:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.3.2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "org.rubilnik.room_service.App"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
