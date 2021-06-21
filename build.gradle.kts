import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


group = "com.profitus"
version = "0.0.1-SNAPSHOT"

buildscript {
	repositories {
		mavenCentral()
	}
}

apply {
	plugin("io.spring.dependency-management")
}

tasks.withType<JavaCompile> {
	sourceCompatibility = "1.8"
	targetCompatibility = "1.8"
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

plugins {
	id("org.springframework.boot") version "2.2.2.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.3.50"
	kotlin("plugin.spring") version "1.3.50"
	kotlin("plugin.jpa") version "1.3.61"
	kotlin("plugin.noarg") version "1.3.61"
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "Hoxton.SR1"

dependencies {
	implementation ("org.springframework.boot:spring-boot-starter")
	implementation ("org.springframework.boot:spring-boot-starter-web")
	implementation ("org.springframework.boot:spring-boot-starter-webflux")
/*	implementation("org.springframework.boot:spring-boot-starter-data-jpa")*/
/*	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("org.springframework.cloud:spring-cloud-starter-config")*/
/*	implementation("org.axonframework:axon-spring-boot-starter:4.2")*/
    implementation ("org.jetbrains.kotlin:kotlin-reflect")
	implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation ("commons-fileupload:commons-fileupload:1.4")
	implementation ("org.apache.poi:poi:4.1.1")
	implementation ("org.apache.poi:poi-ooxml:4.1.1")
	implementation ("org.apache.commons:commons-csv:1.7")
/*	implementation("io.springfox:springfox-swagger-ui:2.9.2")
	implementation("io.springfox:springfox-swagger2:2.9.2")*/
	implementation("io.springfox:springfox-boot-starter:3.0.0")
	testRuntimeOnly("com.h2database:h2")
/*	runtimeOnly ("mysql:mysql-connector-java")*/
/*	implementation("org.springframework.cloud:spring-cloud-starter-config")*/
/*	implementation("org.springframework.cloud:spring-cloud-starter-kubernetes")
	implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-ribbon")*/
/*	implementation("org.springframework.kafka:spring-kafka") */
	testImplementation("io.cucumber:cucumber-java8:4.8.0")
	testImplementation("io.cucumber:cucumber-spring:4.8.0")
	testImplementation("io.cucumber:cucumber-junit:4.8.0")
	testImplementation ("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}
