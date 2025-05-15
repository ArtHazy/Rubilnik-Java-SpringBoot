import org.jetbrains.compose.desktop.application.dsl.TargetFormat

// plugins {
//     application
//     id("org.springframework.boot") version "3.3.2"
// }

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.0"
//    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.guava)

    // no need 'cause using .jar resources
//    implementation(project(":__core"))
//    implementation(project(":_auth-service"))
//    implementation(project(":_room-service"))

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
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi)
            packageName="Rubilnik_Launcher"
            packageVersion="1.0.0"

            macOS {
                signing.sign.set(false)
                iconFile.set { project.file("src/main/resources/icons/app-icon.icns") }
            }
            windows {
                iconFile.set { project.file("src/main/resources/icons/app-icon.ico") }
            }
        }
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

//application {
// mainClass.set("com.example.ui.MainKt")
//}


tasks.register("includeServiceJars",Copy::class) {
    dependsOn(":_auth-service:build")
    dependsOn(":_room-service:build")

    val services = listOf("_auth-service", "_room-service")

    services.forEach { serviceName ->
        from("../$serviceName/build/libs") {
            include("$serviceName.jar")
        }
    }

    into("src/main/resources/jars")

    doFirst {
        println("Copying jars from services: ${services.joinToString()}")
    }
}

tasks.named("processResources") {
    dependsOn("includeServiceJars")
}