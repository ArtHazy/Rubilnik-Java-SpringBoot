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
    implementation("org.springframework.boot:spring-boot-starter-security:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.2")

    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.3.2")

    implementation("org.postgresql:postgresql:42.7.3")
    // implementation("org.hibernate.orm:hibernate-core:6.6.0.Final")
    implementation("org.json:json:20240303")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "org.rubilnik.auth_service.App"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

val reactProjPath = "../../../rubilnik-js-react/react-project"
//tasks.register("compileReactUI",Exec::class) {
//    workingDir=file(reactProjPath)
//    println("reactProjPath (abs) ${workingDir.absolutePath}")
//    commandLine = listOf("npm","run","build")
//    // Only run if the input files changed
//    inputs.files(fileTree(reactProjPath).exclude("**/node_modules/**", "**/dist/**"))
//    outputs.dir("${reactProjPath}/dist")
//}
tasks.register("includeReactUI", Copy::class){
//    dependsOn(":compileReactUI")
//    doFirst{
//        println("cleanup")
//        project.delete(files("src/main/resources/templates/index.html","src/main/resources/assets"))
//    }

    doFirst {
        println("doFirst")
        project.delete(
            layout.projectDirectory.file("src/main/resources/templates/index.html"),
            fileTree(layout.projectDirectory.dir("src/main/resources/assets"))
        )
    }

    from("${reactProjPath}/dist/assets") {
        include("**/*") // Include all
        into("public/assets")
    }
    from("${reactProjPath}/dist") {
        include("index.html")
        into("templates")
    }
    into("src/main/resources")
}
tasks.named("processResources") {
    dependsOn("includeReactUI")
}