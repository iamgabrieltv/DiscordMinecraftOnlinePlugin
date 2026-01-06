plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
}

group = "dev.iamgabriel"
version = "1.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("net.dv8tion:JDA:6.2.1") {
        exclude(module = "opus-java")
        exclude(module="tink")
    }
    implementation("org.bstats:bstats-bukkit:3.1.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations.runtimeClasspath.get())
    relocate("org.bstats", project.group.toString())
    relocate("net.dv8tion", project.group.toString())
    minimize()
}