plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.11"
}

group = "org.dreeam.expansion.folia"
version = "1.0.0"
description = "PlaceholderAPI expansion for Folia"

repositories {
    mavenCentral()

    maven {
        name = "paper-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "placeholderapi"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.5") {
        exclude(group = "org.bstats")
    }

    paperweight.foliaDevBundle("1.19.4-R0.1-SNAPSHOT")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
