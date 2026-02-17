<div style="text-align: center; margin-bottom: 20px;">
  <img src="/content/2.2.0/wiki/assets/logo.png" alt="Charts Library Logo" style="max-width: 500px;">
</div>

# Charts 2.2.0

This release adds a new chart type, improves chart controls, and introduces modular publishing options.

<div style="text-align: center; margin: 2rem 0;">
  <img src="/content/2.2.0/wiki/assets/demo.png" alt="Charts Demo" style="max-width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>


## What's New in 2.2.0

### Release highlights

- Introduced modular publishing + BOM support, while keeping the umbrella dependency option.
- Improved large-data chart handling with smoother scrolling and zoom across all chart types.
- Added axes support across all chart types for clearer labels and easier reading.
- Added new line chart animation modes (`Morph`, `Timeline`) for different data update flows.
- Added `StackedAreaChart` as a new core chart type.
- Improved the [demo gallery](/demo/2.2.0/) layout and navigation for easier browsing.
- Updated [examples](/2.2.0/wiki/examples) with cleaner, default-focused chart demos.
- Launched a charts [Playground](/playground/).

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

## Breaking Changes / Migration

- Breaking changes: None found when comparing `2.1.0` API docs to `2.2.0`.
- Migration: No required changes from `2.1.0`.

## Getting Started

New to Charts? Check out our [Getting Started Guide](/2.2.0/wiki/getting-started) to learn how to integrate the library into your project and create your first charts in minutes.
