# Contributing to charts

Contributions are welcome.

This repository contains many subprojects, including chart libraries, demo apps, docs and playground. There are many ways to contribute, such as feature work, bug fixes, documentation updates, and test improvements.

## Project hierarchy
```text
ChartsProject
├── Core
│   └── :charts-core
├── Umbrella
│   └── :charts
├── Independent Charts
│   ├── :charts-line
│   ├── :charts-pie
│   ├── :charts-bar
│   ├── :charts-radar
│   ├── :charts-stacked-bar
│   └── :charts-stacked-area
├── BOM
│   └── :charts-bom
└── Apps
    ├── :app (common)
    ├── :androidApp
    ├── :iosApp
    ├── :playground
    └── :docs/docs-app
```

## Report issues
Open an issue: https://github.com/dautovicharis/charts/issues

## Technologies by module
| Module | Technologies / Languages |
| --- | --- |
| `:charts-*` | Kotlin Multiplatform, Compose Multiplatform (Android/iOS/JVM/JS) |
| `:charts-bom` | Gradle Java Platform (BOM), Maven Publishing |
| `:app` | Kotlin Multiplatform, Compose Multiplatform (Android/iOS/JVM/JS) |
| `:androidApp` | Kotlin, Android, Jetpack Compose |
| `:iosApp` | Swift, SwiftUI, Xcode |
| `:playground` | Kotlin Multiplatform (JVM/JS), Compose Multiplatform |
| `:docs` | Dokka-generated API docs, Markdown content |
| `:docs/docs-app` | TypeScript, Next.js, React, MDX |

## Test types in this project
- Kotlin/JVM unit tests (core and chart modules).
- Compose UI tests for chart modules (`:charts-*`, KMP Compose UI test APIs).
- Kotlin/JS tests (playground and JS targets).
- Android instrumented tests for deterministic GIF frame capture (`:androidApp`, runs on device/emulator).
- Android screenshot tests (baseline image validation).
- Smoke compile checks (module-level compile validation).
- Behavior/contract tests for docs release links and assets.
