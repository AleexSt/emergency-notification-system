plugins {
    java
    id("org.springframework.boot") version "3.3.6"  // ← меняем на совместимую версию
    id("io.spring.dependency-management") version "1.1.7"
}

version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// ✅ Добавляем Spring Cloud BOM
ext {
    set("springCloudVersion", "2023.0.3")  // Совместим с Spring Boot 3.3.x
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.projectlombok:lombok")

    annotationProcessor("org.projectlombok:lombok")

    implementation("org.telegram:telegrambots:6.5.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// ✅ Добавляем dependencyManagement для Spring Cloud
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
//plugins {
//    java
//    id("org.springframework.boot") version "3.5.7"
//    id("io.spring.dependency-management") version "1.1.7"
//}
//
//version = "1.0.0"
//
//repositories {
//    mavenCentral()
//}
//
//java {
//    toolchain {
//        languageVersion = JavaLanguageVersion.of(21)
//    }
//}
//
//dependencies {
////    implementation(project(":common-libs"))
//
//    implementation("org.springframework.boot:spring-boot-starter-web")
//    implementation("org.springframework.kafka:spring-kafka")
//    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.3")
////
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    runtimeOnly("org.postgresql:postgresql")
//
//    testImplementation("org.springframework.boot:spring-boot-starter-test")
//
//    // lombok
//    implementation("org.projectlombok:lombok")
//    annotationProcessor("org.projectlombok:lombok")
//    testCompileOnly("org.projectlombok:lombok")
//    testAnnotationProcessor("org.projectlombok:lombok")
//
//    // additional libs
//    implementation("org.mapstruct:mapstruct:1.6.3")
//    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
//    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.0")
//    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
//}
//
//tasks.withType<Test> {
//    useJUnitPlatform()
//}