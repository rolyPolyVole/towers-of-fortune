import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.bukkitPluginYaml

plugins {
    kotlin("jvm") version "2.2.0-RC2"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.2.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

group = "dev.rolyPolyVole"
version = "1.0-SNAPSHOT"
description = "A Minecraft minigame"

val pluginName = "TowersOfFortune"
val paperApiVersion = "1.21.6"
val mainClassPath ="$group.towersoffortune.TowersOfFortune"

paperPluginYaml {
    name = pluginName
    description = project.description
    authors = listOf("Esoteric Foundation", "rolyPolyVole", "Esoteric Enderman")
    website = "https://github.com/fireworkwars/lobby-plugin"

    apiVersion = paperApiVersion

    main = mainClassPath
}

bukkitPluginYaml {
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    paperweight.paperDevBundle("$paperApiVersion-R0.1-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.7.0")
}

val targetJavaVersion = 21

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    shadowJar {
        minimize {
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect"))
        }
    }

    build {
        dependsOn(shadowJar)
    }

    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}
