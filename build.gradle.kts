/*
 *
 * build.gradle.kts is based on this : https://github.com/StckOverflw/TwitchControlsMinecraft/blob/4bf406893544c3edf52371fa6e7a6cc7ae80dc05/build.gradle.kts
 *
 * This is one of the ways I tried to include no-mod libraries.
 *
 * This build file is the build im using right now, and it seems to work
 *
 * To use this file as build file, have a look at the settings.gradle.kts file
 *
 */

@file:Suppress("GradlePackageVersionRange")

import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("fabric-loom") version "0.12-SNAPSHOT"
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
    idea
}

base {
    archivesName.set(properties["archives_name"].toString())
    group = property("maven_group")!!
    version = property("mod_version")!!
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${properties["yarn_mappings"]}:v2")

    modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric_version"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${properties["fabric_kotlin_version"]}")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.7.10")
}
tasks {

    val javaVersion = JavaVersion.VERSION_17

    processResources {
        inputs.property("version", project.version)
        filteringCharset = "UTF-8"
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    java {
        withSourcesJar()
    }

    named<Wrapper>("wrapper") {
        gradleVersion = "7.5.1"
        distributionType = Wrapper.DistributionType.ALL
    }

    named<KotlinCompile>("compileKotlin") {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }

    named<JavaCompile>("compileJava") {
        options.encoding = "UTF-8"
        options.release.set(javaVersion.toString().toInt())
    }

    named<Jar>("jar") {
        from("LICENSE") {
            rename { "${it}_${archivesName}" }
        }
    }

    named<Test>("test") { // https://stackoverflow.com/questions/40954017/gradle-how-to-get-output-from-test-stderr-stdout-into-console
        useJUnitPlatform()

        testLogging {
            outputs.upToDateWhen { false } // When the build task is executed, stderr-stdout of test classes will be show
            showStandardStreams = true
        }

        doLast {
            println("Displaying some default values:")
            println("\tshowStackTraces: ${testLogging.showStackTraces}")
            println("\tshowExceptions: ${testLogging.showExceptions}")
            println("\tshowCauses: ${testLogging.showCauses}")
            println("\tshowStandardStreams: ${testLogging.showStandardStreams}")
        }
    }

    val copyJarToTestServer = register("copyJarToTestServer") {
        println("copy to server")
        copyFile("build/libs/TinyEconomyRenewed-1.0.2+1.19.2.jar", project.property("testServerModsFolder") as String)
    }

    build {
        doLast {
            copyJarToTestServer.get()
        }
    }

}

fun copyFile(src: String, dest: String) {
    copy {
        from(src)
        into(dest)
    }
}