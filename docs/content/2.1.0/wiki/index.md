<div style="text-align: center; margin-bottom: 20px;">
  <img src="/content/2.1.0/wiki/assets/logo.png" alt="Charts Library Logo" style="max-width: 500px;">
</div>

# Charts 2.1.0

Charts 2.1.0 focuses on better chart capabilities, more predictable interactions, and a cleaner docs/demo experience for day-to-day development.

<div style="text-align: center; margin: 2rem 0;">
  <img src="/content/2.1.0/wiki/assets/demo.png" alt="Charts Demo" style="max-width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>


## What's New in 2.1.0

### Release Highlights
- `RadarChart` is now part of the core chart set for multivariate comparisons.
- Improved line rendering, drag markers, and selection accuracy.
- Nested scroll input handling is more stable, reducing gesture conflicts.
- Data handling is more reliable with clearer normalization and validation behavior.
- Pie and radar interactions are more predictable for preselection, legend visibility, and title behavior.

### Chart Library Improvements
- **RadarChart support**: Added radar visualization with dedicated style options and preview support.
- **Interaction accuracy**: Fixed Bezier drag mapping and bar selection hit testing for spaced layouts.
- **State stability**: Reduced gesture conflicts in scroll containers and improved state/data update consistency.
- **Data quality**: Added normalization support and clearer validation messaging for minimum bar data requirements.
- **Pie/Radar polish**: Improved pie preselection and selection UX; fixed radar legend visibility when `categoryLegendVisible` is disabled.

#### RadarChart Quick Start

```kotlin
RadarChart(
    data = listOf(
        "Speed" to listOf(0.9f, 0.7f, 0.8f),
        "Power" to listOf(0.6f, 0.9f, 0.7f),
        "Control" to listOf(0.8f, 0.6f, 0.9f)
    ),
    legends = listOf("Player A", "Player B", "Player C")
)
```

- See more setup and variants in [Examples](/2.1.0/wiki/examples)
- Try it live in the [Demo Gallery](/2.1.0/playground)

### Demo Experience
- Improved gallery navigation and responsive behavior across screen sizes.
- Added a style-details dialog for exploring chart style parameters in demo screens.

### Quality and Reliability
- Expanded screenshot-based visual checks with shared fixtures and baseline references.
- Added tablet portrait/landscape coverage for stronger visual regression confidence.

### Documentation and Website
- Introduced a versioned docs platform for clearer release-to-release navigation.
- Improved docs/deployment reliability and refreshed documentation links and structure.

### Internal Improvements
- Improved internal build structure and CI/tooling for consistent delivery and verification workflows.


## Breaking Changes / Migration

- Breaking changes: None in this version
- Migration: No action required from `2.0.x`


## Getting Started

New to Charts? Check out our [Getting Started Guide](/2.1.0/wiki/getting-started) to learn how to integrate the library into your project and create your first charts in minutes.

## Documentation

- [API Documentation](/2.1.0/api) - Detailed API reference
