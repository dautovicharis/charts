plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.jetbrainsCompose)
    `maven-publish`
    signing
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(
        libs.versions.java
            .get()
            .toInt(),
    )

    android {
        namespace = Config.CHARTS_DEMO_SHARED_NAMESPACE
        compileSdk = Config.COMPILE_SDK
        minSdk = Config.MIN_SDK
        androidResources {
            enable = true
        }
        compilerOptions {
            jvmTarget.set(
                org.jetbrains.kotlin.gradle.dsl.JvmTarget
                    .fromTarget(libs.versions.java.get()),
            )
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    js(IR) {
        browser()
        binaries.executable()
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            api(projects.charts)
            api(projects.chartsCore)
            api(libs.compose.mpp.runtime)
            api(libs.compose.mpp.foundation)
            api(libs.compose.mpp.material3)
            api(libs.compose.mpp.ui)
            api(libs.compose.mpp.resources)
            api(libs.kotlinx.collections.immutable)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = Config.GROUP_ID,
        artifactId = Config.ARTIFACT_DEMO_SHARED_ID,
        version = project.version.toString(),
    )

    pom {
        ChartsPublishing.configurePom(
            pom = this,
            moduleName = "Charts Shared Demo Resources",
            moduleDescription = "Shared demo resources, theme, and sample data for Charts app and playground.",
        )
    }
}

compose.resources {
    publicResClass = true
}
