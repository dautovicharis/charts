
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

### Snapshot Builds [![Snapshot](https://img.shields.io/nexus/s/io.github.dautovicharis/charts?server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/dautovicharis/charts/)
Access the latest pre-release builds through the Sonatype snapshots repository. Snapshots contain the most recent features and fixes that haven't been officially released yet, allowing you to test upcoming functionality.
```kotlin
commonMain.dependencies {
    implementation("io.github.dautovicharis:charts:<snapshot-version>")
}

dependencyResolutionManagement {
    repositories {
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}
```
## Next Steps
- [Code Examples](examples.md) - Learn how to create and customize different chart types with detailed code samples.