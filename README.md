<p align="center">
  <img
    src="https://github.com/dautovicharis/Charts/assets/7049715/4150f102-1b05-4fd7-ab01-63480d2e6d50"
    alt="Charts logo"
    width="300"
  />
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/io.github.dautovicharis/charts/overview">
    <img src="https://img.shields.io/maven-central/v/io.github.dautovicharis/charts.svg?label=Maven%20Central" />
  </a>
  <a href="https://central.sonatype.com/repository/maven-snapshots/io/github/dautovicharis/charts/maven-metadata.xml">
    <img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fgithub%2Fdautovicharis%2Fcharts%2Fmaven-metadata.xml&label=Snapshots&color=4285F4" />
  </a>
  <img src="https://img.shields.io/badge/Jetpack_Compose-1.9.2-4285F4?logo=jetpackcompose" />
</p>

<p align="center">
  A Kotlin Multiplatform chart library built with Jetpack Compose.
</p>

<p align="center">
<img width="1200" height="913" alt="Screenshot 2026-02-12 at 00 55 05" src="https://github.com/user-attachments/assets/b547a36f-d318-47ff-a7e1-f374e8b5eebb" />

</p>

---

## Get Started

Add Charts to `commonMain`:

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

Basic example:

<img src="docs/content/snapshot/wiki/assets/line_default.gif" alt="Basic line chart example" height="236" align="right" style="margin-top: 28px;" />

```kotlin
@Composable
fun BasicLineChart() {
    val values = listOf(42f, 38f, 45f, 51f, 47f, 54f, 49f)
    val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val dataSet = values.toChartDataSet(
        title = "Daily Support Tickets",
        labels = labels,
    )

    LineChart(dataSet)
}
```

<br clear="right" />

## Documentation
https://charts.harisdautovic.com/

## Production Demo (JS)
https://charts.harisdautovic.com/static/demo/2.1.0/index.html

## Snapshot Demo (JS)
https://charts.harisdautovic.com/static/demo/snapshot/index.html
