<div style="text-align: center; margin-bottom: 20px;">
  <img src="/content/snapshot/wiki/assets/logo.png" alt="Charts Library Logo" style="max-width: 500px;">
</div>

# Charts 2.2.0-SNAPSHOT

<div style="text-align: center; margin: 2rem 0;">
  <img src="/content/snapshot/wiki/assets/demo.gif" alt="Charts Demo" style="max-width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>


## What's New in 2.2.0-SNAPSHOT

This snapshot includes both chart improvements and modular publishing.

### Chart updates

- Added `StackedAreaChart` with absolute cumulative stacking for multi-series trend visualization.
- Expanded demo gallery, screenshot coverage, and examples with stacked area basic/custom variants.
- Reworked `BarChart` rendering and interaction behavior for better dense-data handling.
- Added automatic scroll/zoom mode for large bar datasets (50+ points), including pinch and double-tap zoom.
- Expanded `BarChartDefaults.style(...)` with axis/grid/label and selection indicator customization.
- Updated bar selection behavior to toggle on tap and show selected `label: value` in the chart title.

### Modular publishing updates

Use the umbrella artifact (same behavior as before):

```kotlin
implementation("io.github.dautovicharis:charts:<version>")
```

Use modular artifacts (pick what you need):

```kotlin
implementation("io.github.dautovicharis:charts-core:<version>")
implementation("io.github.dautovicharis:charts-line:<version>")
implementation("io.github.dautovicharis:charts-pie:<version>")
implementation("io.github.dautovicharis:charts-bar:<version>")
implementation("io.github.dautovicharis:charts-stacked-bar:<version>")
implementation("io.github.dautovicharis:charts-stacked-area:<version>")
implementation("io.github.dautovicharis:charts-radar:<version>")
```

Use BOM for aligned versions (where Gradle platforms are supported):

```kotlin
implementation(platform("io.github.dautovicharis:charts-bom:<version>"))
implementation("io.github.dautovicharis:charts-core")
implementation("io.github.dautovicharis:charts-line")
implementation("io.github.dautovicharis:charts-pie")
implementation("io.github.dautovicharis:charts-bar")
implementation("io.github.dautovicharis:charts-stacked-bar")
implementation("io.github.dautovicharis:charts-stacked-area")
implementation("io.github.dautovicharis:charts-radar")
```

## Getting Started

New to Charts? Check out our [Getting Started Guide](/snapshot/wiki/getting-started) to learn how to integrate the library into your project and create your first charts in minutes.

## Documentation

- [API Documentation](/snapshot/api) - Detailed API reference
