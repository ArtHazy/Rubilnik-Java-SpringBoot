plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.guava)
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// application {
//     mainClass = "org.rubilnik.core.App"
// }

tasks.named<Test>("test") {
    useJUnitPlatform()
}
