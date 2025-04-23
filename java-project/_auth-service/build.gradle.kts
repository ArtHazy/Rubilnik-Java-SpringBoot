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
    implementation("org.springframework.boot:spring-boot-starter-aop:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.2")

    implementation("org.postgresql:postgresql:42.7.3")
    // implementation("org.hibernate.orm:hibernate-core:6.6.0.Final")
    implementation("org.json:json:20240303")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "org.rubilnik.auth_service.App"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
