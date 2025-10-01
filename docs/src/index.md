<div style="text-align: center; margin-bottom: 20px;">
  <img src="assets/logo.png" alt="Charts Library Logo" style="max-width: 500px;">
</div>

# Charts 2.0.1-SNAPSHOT
Welcome to the Charts documentation! This library provides a simple way to create beautiful charts in Kotlin Multiplatform applications.

# What's New in this version

Library
- [Fix ProGuard configuration](https://github.com/dautovicharis/charts/issues/143) ([#225](https://github.com/dautovicharis/Charts/pull/225))
- [Update Compose Version to 1.9.2](https://github.com/dautovicharis/charts/issues/232) ([#226](https://github.com/dautovicharis/charts/pull/234))

Docs
- Restore/fix Dokka documentation generation ([#230](https://github.com/dautovicharis/Charts/pull/230))
- Content updates: README and wiki URLs; updated contributing guide ([#222](https://github.com/dautovicharis/Charts/pull/222), [#223](https://github.com/dautovicharis/Charts/pull/223), [#229](https://github.com/dautovicharis/Charts/pull/229), [#231](https://github.com/dautovicharis/Charts/pull/231))
- Docs improvements ([#226](https://github.com/dautovicharis/Charts/pull/226))

Demo app
- Improve theme handling with dark mode and dynamic colors ([#220](https://github.com/dautovicharis/Charts/pull/220))

CI/CD workflows
- Add and refine snapshot release workflow ([#235](https://github.com/dautovicharis/Charts/pull/235), [#236](https://github.com/dautovicharis/Charts/pull/236))
- Fix and refine docs deploy and JS demo generation ([#227](https://github.com/dautovicharis/Charts/pull/227), [#228](https://github.com/dautovicharis/Charts/pull/228), [#237](https://github.com/dautovicharis/Charts/pull/237))

Build & dependencies
- Update dependencies and Gradle wrapper to latest versions ([#234](https://github.com/dautovicharis/Charts/pull/234))

<div style="text-align: center; margin: 2rem 0;">
  <img src="assets/demo.gif" alt="Charts Demo" style="max-width: 100%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
</div>

## Getting Started

New to Charts? Check out our [Getting Started Guide](getting-started.md) to learn how to integrate the library into your project and create your first charts in minutes.

## Interactive JS Demo
Explore the capabilities of Charts library with our interactive demo. This live showcase demonstrates all chart types and customization options available in the current version.
<div style="text-align: center; margin: 1.5rem 0;">
    <a href="jsdemo/index.html" target="_blank" style="display: inline-flex; align-items: center; background-color: var(--md-primary-fg-color); color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px; font-weight: 500; font-size: 14px; box-shadow: 0 1px 3px rgba(0,0,0,0.12); transition: all 0.2s ease;">
        <span class="twemoji">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16" style="margin-right: 8px;">
                <path fill="currentColor" d="M8 5v14l11-7z"></path>
            </svg>
        </span>
        Try Interactive Demo
    </a>
</div>


## Documentation
- [API Documentation](api/index.html) - Detailed API reference
- [Interactive Demo](jsdemo/index.html) - Explore all chart types and customization options
- [Examples](examples.md) - Code samples
- [Example Project](https://github.com/dautovicharis/Charts/tree/main/app/src/commonMain/kotlin/io/github/dautovicharis/charts/app/demo)