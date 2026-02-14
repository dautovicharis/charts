plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(
        libs.versions.java
            .get()
            .toInt(),
    )
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.chartsLine)
        }
    }
}
