import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    java
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    id("io.spring.javaformat") version "0.0.41"
    id("net.ltgt.errorprone") version "3.1.0"
    id("checkstyle")
}

group = "wf.garnier"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("com.google.cloud:spring-cloud-gcp-starter-data-datastore:5.4.3")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("net.sourceforge.htmlunit:htmlunit")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    checkstyle("io.spring.javaformat:spring-javaformat-checkstyle:0.0.41")

    errorprone("com.google.errorprone:error_prone_core:2.27.1")
    errorprone("com.uber.nullaway:nullaway:0.10.26")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    // Required for Spring Boot method param reflection
    options.compilerArgs.add("-parameters")

    // Enable null checks
    options.errorprone {
        check("NullAway", CheckSeverity.ERROR)
        option("NullAway:AnnotatedPackages", "com.vmware.tanzu.apps.sso")
    }
}

checkstyle {
    toolVersion = "10.15.0"
}