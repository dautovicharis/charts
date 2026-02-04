rootProject.name = "ChartsProject"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // https://github.com/gradle/foojay-toolchains
    // Automatically download required JDK
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        // Central Portal Snapshots repository (replaces old OSSRH snapshots)
        maven("https://central.sonatype.com/repository/maven-snapshots/") {
            name = "Central Portal Snapshots"
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}

include(":app")
include(":androidApp")
include(":charts")
include(":docs")
 
