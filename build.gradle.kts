plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
}

group = "dev.iamgabriel"
version = "1.0-SNAPSHOT"

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
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}