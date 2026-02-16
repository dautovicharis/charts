import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
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
                implementation(libs.compose.mpp.runtime)
                implementation(libs.compose.mpp.ui)
                implementation(
                    "org.jetbrains.kotlin:kotlin-compiler-embeddable:${libs.versions.kotlin.multiplatform.get()}",
                )
            }
        }

        commonMain.dependencies {
            implementation(libs.compose.mpp.runtime)
            implementation(libs.compose.mpp.foundation)
            implementation(libs.compose.mpp.material3)
            implementation(libs.compose.mpp.material.icons.extended)
            implementation(libs.compose.mpp.ui)
            implementation(libs.compose.mpp.resources)
            implementation(project(":app"))
            implementation(project(":charts"))
        }

        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

buildConfig {
    packageName("ui")
    buildConfigField("CHARTS_VERSION", Config.chartsVersion)
    useKotlinOutput()
}
