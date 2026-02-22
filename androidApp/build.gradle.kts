import org.gradle.api.tasks.testing.Test
import java.io.File
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.composeScreenshot)
    alias(libs.plugins.ksp)
    alias(libs.plugins.composeGifRecorder)
}

val localSigningProperties =
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use(::load)
        }
    }

fun Project.readSigningProperty(name: String): String? {
    val gradleProperty = providers.gradleProperty(name).orNull
    if (!gradleProperty.isNullOrBlank()) return gradleProperty

    val localProperty = localSigningProperties.getProperty(name)
    return localProperty?.takeIf { it.isNotBlank() }
}

fun Project.resolveSigningFile(path: String): File {
    val file = File(path)
    return if (file.isAbsolute) file else rootProject.file(path)
}

val releaseStoreFilePath = project.readSigningProperty("ANDROID_RELEASE_STORE_FILE")
val releaseStorePassword = project.readSigningProperty("ANDROID_RELEASE_STORE_PASSWORD")
val releaseKeyAlias = project.readSigningProperty("ANDROID_RELEASE_KEY_ALIAS")
val releaseKeyPassword = project.readSigningProperty("ANDROID_RELEASE_KEY_PASSWORD")

val hasReleaseSigningConfig =
    listOf(
        releaseStoreFilePath,
        releaseStorePassword,
        releaseKeyAlias,
        releaseKeyPassword,
    ).all { !it.isNullOrBlank() }

val gifDocsVersion = providers.gradleProperty("gifDocsVersion").orElse("snapshot")
val protobufSecurityVersion =
    libs.versions.protobuf.security
        .get()
val nettySecurityVersion =
    libs.versions.netty.codec.http2.security
        .get()
val commonsLang3SecurityVersion =
    libs.versions.commons.lang3.security
        .get()
val httpClientSecurityVersion =
    libs.versions.httpclient.security
        .get()
val guavaSecurityVersion =
    libs.versions.guava.security
        .get()
val jose4jSecurityVersion =
    libs.versions.jose4j.security
        .get()

configurations.configureEach {
    if (name.startsWith("_internal-unified-test-platform")) {
        resolutionStrategy.eachDependency {
            if (requested.group == SecurityOverrides.protobufGroup &&
                requested.name in SecurityOverrides.protobufArtifacts
            ) {
                useVersion(protobufSecurityVersion)
                because(SecurityOverrides.protobufReason)
            }
            if (requested.group == SecurityOverrides.nettyGroup &&
                requested.name == SecurityOverrides.nettyHttp2Artifact
            ) {
                useVersion(nettySecurityVersion)
                because(SecurityOverrides.nettyHttp2Reason)
            }
            if (requested.group == SecurityOverrides.nettyGroup &&
                requested.name == SecurityOverrides.nettyCodecArtifact
            ) {
                useVersion(nettySecurityVersion)
                because(SecurityOverrides.nettyCodecReason)
            }
            if (requested.group == SecurityOverrides.nettyGroup &&
                requested.name == SecurityOverrides.nettyHttpArtifact
            ) {
                useVersion(nettySecurityVersion)
                because(SecurityOverrides.nettyHttpReason)
            }
            if (requested.group == SecurityOverrides.commonsLangGroup &&
                requested.name == SecurityOverrides.commonsLang3Artifact
            ) {
                useVersion(commonsLang3SecurityVersion)
                because(SecurityOverrides.commonsLang3Reason)
            }
            if (requested.group == SecurityOverrides.httpComponentsGroup &&
                requested.name == SecurityOverrides.httpClientArtifact
            ) {
                useVersion(httpClientSecurityVersion)
                because(SecurityOverrides.httpClientReason)
            }
            if (requested.group == SecurityOverrides.guavaGroup &&
                requested.name == SecurityOverrides.guavaArtifact
            ) {
                useVersion(guavaSecurityVersion)
                because(SecurityOverrides.guavaReason)
            }
            if (requested.group == SecurityOverrides.jose4jGroup &&
                requested.name == SecurityOverrides.jose4jArtifact
            ) {
                useVersion(jose4jSecurityVersion)
                because(SecurityOverrides.jose4jReason)
            }
        }
    }
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

    signingConfigs {
        if (hasReleaseSigningConfig) {
            create("release") {
                storeFile = project.resolveSigningFile(checkNotNull(releaseStoreFilePath))
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            if (hasReleaseSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    kotlin {
        jvmToolchain(
            libs.versions.java
                .get()
                .toInt(),
        )
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

gifRecorder {
    applicationId.set(Config.demoNamespace)
    outputDir.set(
        gifDocsVersion.map { docsVersion ->
            rootProject.layout.projectDirectory.dir("docs/content/$docsVersion/wiki/assets")
        },
    )
}

dependencies {
    implementation(project(":app"))
    implementation(project(":charts"))
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
