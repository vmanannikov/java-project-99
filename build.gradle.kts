import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	java
	checkstyle
	id("io.freefair.lombok") version "8.6"
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
	id("com.github.ben-manes.versions") version "0.50.0"
	id("io.sentry.jvm.gradle") version "4.7.1"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

/* Swagger */
dependencies {
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
	implementation("org.springdoc:springdoc-openapi-ui:1.8.0")
}

/* Spring */
dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-devtools")
}

/* Validation */
dependencies {
	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
	implementation("jakarta.validation:jakarta.validation-api:3.0.2")
}

/* Mapper */
dependencies {
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
}

/* Jackson Nullable */
dependencies {
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
}

/* H2 */
dependencies {
	implementation("com.h2database:h2:2.2.224")
}

/* Postgresql */
dependencies {
	runtimeOnly("org.postgresql:postgresql")
}

/* Faker */
dependencies {
	implementation("net.datafaker:datafaker:2.2.2")
}

/* Instancio */
dependencies {
	implementation("org.instancio:instancio-junit:3.3.1")
}

/* Spring Tests */
dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

/* JSON JUnit */
dependencies {
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.7")
}

tasks.test {
	useJUnitPlatform()
	testLogging {
		exceptionFormat = TestExceptionFormat.FULL
		events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
		showStandardStreams = true
	}
}

tasks.sentryBundleSourcesJava {
	enabled = System.getenv("SENTRY_AUTH_TOKEN") != null
}

sentry {
	// Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
	// This enables source context, allowing you to see your source
	// code as part of your stack traces in Sentry.
	includeSourceContext = true

	org = "vadim-manannikov"
	projectName = "java-project-99"
	authToken = System.getenv("SENTRY_AUTH_TOKEN")

	// Automatically adds Sentry dependencies to your project.
	autoInstallation {
		enabled.set(true)
	}
}
