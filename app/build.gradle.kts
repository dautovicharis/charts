import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.build.config)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(
        libs.versions.java
            .get()
            .toInt(),
    )
    android {
        namespace = Config.demoLibraryNamespace
        compileSdk = Config.compileSdk
        minSdk = Config.minSdk
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
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "app"
            isStatic = true
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "Charts.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).copy()
            }
            binaries.executable()
        }
    }
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.mpp.runtime)
            implementation(libs.compose.mpp.foundation)
            implementation(libs.compose.mpp.material3)
            implementation(libs.compose.mpp.ui)
            implementation(libs.compose.mpp.resources)
            implementation(libs.compose.mpp.material.icons.extended)

            implementation(project(":charts"))
            // Snapshot test
            // implementation("io.github.dautovicharis:charts:2.0.0-SNAPSHOT")
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.collections.immutable)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
        }
    }
}

// Required for Desktop
compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = Config.demoNamespace
            packageVersion = Config.demoVersionName
        }
    }
}

// Shared BuildConfig
buildConfig {
    packageName(Config.demoLibraryNamespace)
    buildConfigField("CHARTS_VERSION", Config.chartsVersion)
    useKotlinOutput()
}

compose.resources {
    publicResClass = true
}
