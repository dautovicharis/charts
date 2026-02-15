@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(
        libs.versions.java
            .get()
            .toInt(),
    )

    jvm()

    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "Playground.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).copy()
            }
            binaries.executable()
        }
    }

    sourceSets {
        jvmTest {
            kotlin.srcDir("src/jsMain/kotlin/codegen")
            kotlin.srcDir("src/jsMain/kotlin/model")

            dependencies {
                implementation(kotlin("test"))
                implementation(project(":app"))
                implementation(project(":charts"))
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(
                    "org.jetbrains.kotlin:kotlin-compiler-embeddable:${libs.versions.kotlin.multiplatform.get()}",
                )
            }
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(project(":app"))
            implementation(project(":charts"))
        }

        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
