import org.owasp.dependencycheck.gradle.extension.AnalyzerExtension
import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension
import org.owasp.dependencycheck.reporting.ReportGenerator.Format

plugins {
    id("org.owasp.dependencycheck") version "8.2.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
    id("com.google.cloud.tools.jib") version "3.4.3"
    kotlin("jvm") version "2.0.0"
}

group = "io.jcervelin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

jib {
    val docker_username: String by project
    val docker_password: String by project
    from {
        image = "eclipse-temurin:17.0.12_7-jdk-focal"
    }
    to {
        auth {
            username = docker_username
            password = docker_password
        }
        image = "julianocervelin/chatex"
    }
    container {
        mainClass = "io.ideas.jcervelin.quizex.MainKt"

    }

}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

configure<DependencyCheckExtension> {
    format = Format.XML.toString()
    analyzers(closureOf<AnalyzerExtension> {
        knownExploitedEnabled = false
    })
    hostedSuppressions.enabled = false
}


dependencies {
    val sf4j = "2.0.6"
    implementation(platform("org.http4k:http4k-bom:5.29.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-undertow")
    implementation("org.http4k:http4k-client-apache")
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.11.0")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.7.0")
    implementation("org.http4k:http4k-client-okhttp")
    implementation("org.http4k:http4k-format-kotlinx-serialization")
    implementation("org.slf4j:slf4j-api:${sf4j}")
    implementation("org.slf4j:slf4j-simple:${sf4j}")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}