# Contributing to charts

Contributions are welcome.

This repository contains chart libraries and sample apps. Documentation content and the docs website now live in `HDCharts/charts-docs`. There are many ways to contribute, such as feature work, bug fixes, and test improvements.

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
├── Shared Demo Resources
│   └── :charts-demo-shared
└── Apps
    ├── :app (common)
    ├── :androidApp
    └── :iosApp

Playground
└── moved to https://github.com/HDCharts/charts-playground
```

## Docs repository
- Docs source, version registry, static assets, and Next.js docs site are maintained in `https://github.com/HDCharts/charts-docs`.

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

## Test types in this project
- Kotlin/JVM unit tests (core and chart modules).
- Compose UI tests for chart modules (`:charts-*`, KMP Compose UI test APIs).
- Kotlin/JS tests (playground and JS targets).
- Android instrumented GIF recording via `compose-gif-recorder` Gradle plugin (`:androidApp`, runs on device/emulator).
- Android screenshot tests (baseline image validation).
- Smoke compile checks (module-level compile validation).

## GIF Recording
- List available docs GIF scenarios: `./gradlew listDocsGifScenarios`
- Record one scenario to local `docs/content/snapshot/wiki/assets`: `./gradlew recordDocsGif -PgifScenario=line_default`
- Record all scenarios to local `docs/content/snapshot/wiki/assets`: `./gradlew recordDocsGifs`
- Target a different docs version folder with `-PgifDocsVersion=<version>` (for example `2.2.0`).
- After recording, copy assets to `charts-docs/content/<version>/wiki/assets`.
