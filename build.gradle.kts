plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
    kotlin("jvm") version "2.0.0"
}

group = "io.jcervelin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.http4k:http4k-bom:5.29.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-undertow")
    implementation("org.http4k:http4k-client-apache")
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.11.0")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.7.0")
    implementation("org.http4k:http4k-client-okhttp")
    implementation("org.http4k:http4k-format-kotlinx-serialization")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}