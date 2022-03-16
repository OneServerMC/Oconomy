import org.apache.tools.ant.filters.ReplaceTokens
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.oneserver"

repositories {
    mavenCentral()
    maven { url = uri("https://repo1.maven.org/maven2/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://jitpack.io") }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

application {
    mainClassName = "org.oneserver.oconomy.OconomyKt"
}

tasks.withType<ShadowJar> {
    archiveFileName.set("Oconomy.jar")
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.processResources {
    filteringCharset = "UTF-8"
    from(sourceSets["main"].resources.srcDirs) {
        include("**/*.yml")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filter<ReplaceTokens>("tokens" to mapOf("version" to project.version))
        filter<ReplaceTokens>("tokens" to mapOf("name" to project.name))
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    val exposedVersion: String = "0.37.3"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    implementation("com.github.hazae41:mc-kutils:master-SNAPSHOT")
    implementation("com.github.MilkBowl:VaultAPI:1.7.1")

    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
}