import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
    id("io.github.remodstudios.datakenesis.plugin.greeting")
}

group = "com.remodstudios"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.0")

    testImplementation("io.kotest:kotest-runner-junit5:4.5.0")
}

tasks {
    withType<Test> { useJUnitPlatform() }
    withType<KotlinCompile> {
        kotlinOptions {
            languageVersion = "1.5"
            jvmTarget = "1.8"
        }
    }
}