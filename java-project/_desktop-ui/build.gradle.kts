// plugins {
//     application
//     id("org.springframework.boot") version "3.3.2"
// }

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.0"
    // application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.guava)

    implementation(project(":__core"))
    implementation(project(":_auth-service"))
    implementation(project(":_room-service"))

    implementation("org.jetbrains.compose.desktop:desktop:1.5.0")
    implementation(compose.desktop.currentOs)

    // implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
    // implementation("org.springframework.boot:spring-boot-starter-websocket:3.3.2")
    // implementation("org.springframework.boot:spring-boot-starter-aop:3.3.2")
}

kotlin {
    jvmToolchain(17) // or 11
}
//
//java {
//     toolchain {
//         languageVersion = JavaLanguageVersion.of(17)
//     }
//}

// application {
//     mainClass = "org.rubilnik.ui.desktop.AppKt"
// }

compose.desktop {
    application {
        mainClass = "org.rubilnik.ui.desktop.AppKt" // change this to your actual main class
    }
}

//tasks.named<Test>("test") {
//    useJUnitPlatform()
//}

// application {
//     mainClass.set("com.example.ui.MainKt")
// }