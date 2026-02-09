
# Getting Started with Charts

This guide will help you integrate the Charts library into your Kotlin Multiplatform project.

## Installation

### Core Dependency
Replace `<version>` with the latest version: [![Release](https://img.shields.io/maven-central/v/io.github.dautovicharis/charts.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.dautovicharis/charts/overview)

```kotlin
commonMain.dependencies {
    implementation("io.github.dautovicharis:charts:<version>")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
```

### Platform-Specific Dependencies
```kotlin
implementation("io.github.dautovicharis:charts-android:<version>") // For Android
implementation("io.github.dautovicharis:charts-jvm:<version>")     // For JVM
implementation("io.github.dautovicharis:charts-js:<version>")      // For JavaScript
implementation("io.github.dautovicharis:charts-iosx64:<version>")  // For iOS (x64)
implementation("io.github.dautovicharis:charts-iosarm64:<version>") // For iOS (ARM64)
```

### Snapshot Builds [![Snapshots](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fgithub%2Fdautovicharis%2Fcharts%2Fmaven-metadata.xml&label=Snapshots&color=4285F4)](https://central.sonatype.com/repository/maven-snapshots/io/github/dautovicharis/charts/maven-metadata.xml)
Access the latest pre-release builds through the Sonatype snapshots repository. Snapshots contain the most recent features and fixes that haven't been officially released yet, allowing you to test upcoming functionality.
```kotlin
commonMain.dependencies {
    implementation("io.github.dautovicharis:charts:<snapshot-version>")
}

dependencyResolutionManagement {
    repositories {
    // Sonatype Central Portal Snapshots (replaces old s01.oss.sonatype.org)
    maven("https://central.sonatype.com/repository/maven-snapshots/")
    }
}
```
