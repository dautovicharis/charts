<div style="text-align: center; margin-bottom: 20px;">
  <img src="/content/snapshot/wiki/assets/logo.png" alt="Charts Library Logo" style="max-width: 500px;">
</div>

# Charts 2.0.2-SNAPSHOT

Welcome to the Charts documentation! This library provides a simple way to create beautiful charts in Kotlin Multiplatform applications.

<div style="text-align: center; margin: 2rem 0;">
  <img src="/content/snapshot/wiki/assets/demo.gif" alt="Charts Demo" style="max-width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

## What's New in 2.0.2-SNAPSHOT

### ✨ Features
- **Radar Chart**: Added a new radar chart component for multivariate data visualization ([#255](https://github.com/dautovicharis/Charts/pull/255))
- **Chart Gallery**: Added a gallery to explore available chart types and examples ([#256](https://github.com/dautovicharis/Charts/pull/256))
- **Pie Chart Preselection**: Added `selectedSliceIndex` to support deterministic selected-slice rendering for previews and screenshots ([#272](https://github.com/dautovicharis/Charts/pull/272))
- **Responsive Gallery Layout**: Improved gallery cards with adaptive single/two-column rendering and updated previews for clearer radar comparisons

### 🐛 Fixes
- **Gesture Handling**: Improved touch and scroll interactions to avoid gesture conflicts ([#248](https://github.com/dautovicharis/Charts/pull/248))
- **Line Chart Dragging**: Fixed drag behavior to keep selected points on the curve ([#247](https://github.com/dautovicharis/Charts/pull/247))
- **Bar Chart Selection**: Fixed selection calculations related to bar spacing ([#250](https://github.com/dautovicharis/Charts/pull/250))
- **Chart State Stability**: Improved chart state and data update consistency ([#252](https://github.com/dautovicharis/Charts/pull/252))
- **Line Chart Rendering**: Improved bezier interpolation, reveal animation, and drag marker behavior for smoother visual feedback ([#272](https://github.com/dautovicharis/Charts/pull/272))
- **Radar Chart Legends**: Fixed series legend visibility when `categoryLegendVisible` is disabled

## Getting Started

New to Charts? Check out our [Getting Started Guide](/2.0.1/wiki/getting-started) to learn how to integrate the library into your project and create your first charts in minutes.

## Documentation

- [API Documentation](/snapshot/api) - Detailed API reference
