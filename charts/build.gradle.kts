@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.compose.compiler)
}

buildscript {
    dependencies {
        classpath(libs.dokka.versions)
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
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
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material3)
            api(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.collections.immutable)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation(compose.uiTest)
        }

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.ui.tooling)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    defaultConfig {
        namespace = Config.chartsNamespace
        compileSdk = Config.compileSdk
        minSdk = Config.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("proguard-rules.pro")
    }

    buildFeatures {
        compose = true
    }

    kotlin {
        jvmToolchain(libs.versions.java.get().toInt())
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    }
}

dokka {
    dokkaSourceSets.commonMain {
        sourceLink {
            sourceRoots.setFrom(emptyList<File>())
            sourceRoots.from(file("src/commonMain/kotlin/io/github/dautovicharis/charts"))
            remoteUrl("https://github.com/dautovicharis/charts/tree/${Config.chartsVersion}/charts")
            remoteLineSuffix.set("#L")
        }

        skipDeprecated.set(false)
        skipEmptyPackages.set(true)
    }

    dokkaPublications.html {
        outputDirectory.set(file(project.rootDir.resolve("docs/src/api")))
    }

    pluginsConfiguration {
        versioning {
            version.set(Config.chartsVersion)
        }
    }
}

// https://github.com/Kotlin/dokka/issues/3988
tasks.register("dokkaHtml") {
    dependsOn("dokkaGenerate")
}

mavenPublishing {
    coordinates(
        groupId = Config.groupId,
        artifactId = Config.artifactId,
        version = Config.chartsVersion
    )

    pom {
        name.set("Charts")
        description.set("Charts made in JetpackCompose")
        inceptionYear.set("2024")
        url.set("https://github.com/dautovicharis/Charts")

        licenses {
            license {
                name.set("MIT")
                url.set("https://github.com/dautovicharis/Charts/blob/main/LICENSE")
            }
        }
        developers {
            developer {
                id.set("dautovicharis")
                name.set("Haris Dautović")
                email.set("haris.dautovic.dev@gmail.com")
            }
        }
        issueManagement {
            system.set("GitHub")
            url.set("https://github.com/dautovicharis/Charts/issues")
        }
        scm {
            connection.set("https://github.com/dautovicharis/Charts.git")
            url.set("https://github.com/dautovicharis/Charts")
        }
    }
}

dependencies {
    dokkaHtmlPlugin(libs.dokka.versions)
    dokkaHtmlPlugin(libs.dokka.doc)
}
