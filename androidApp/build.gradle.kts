import org.gradle.api.tasks.testing.Test

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.composeScreenshot)
}

android {
    namespace = Config.demoNamespace
    compileSdk = Config.compileSdk

    defaultConfig {
        applicationId = Config.demoNamespace
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
        versionCode = Config.demoVersionCode
        versionName = Config.demoVersionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
        }
    }

    kotlin {
        jvmToolchain(libs.versions.java.get().toInt())
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    }

    buildFeatures {
        compose = true
    }

    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    testOptions {
        screenshotTests {
            imageDifferenceThreshold = 0.00025f
        }
    }
}

dependencies {
    implementation(project(":app"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.koin.android)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(project(":app"))
    androidTestImplementation(project(":charts"))
    androidTestImplementation(libs.compose.ui.tooling.preview)

    screenshotTestImplementation(libs.screenshot.validation.api)
    screenshotTestImplementation(libs.compose.ui.tooling.preview)
    screenshotTestImplementation(libs.compose.ui.tooling)
    screenshotTestImplementation(project(":charts"))
    screenshotTestImplementation(project(":app"))
}

tasks.withType<Test>().configureEach {
    if (name.contains("ScreenshotTest")) {
        // Screenshot rendering is memory-heavy; keep fork count low and heap high to avoid OOM.
        maxParallelForks = 1
        maxHeapSize = "3g"
    }
}
