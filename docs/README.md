# Documentation Management

This directory contains the documentation for the Charts library, built using Dokka, MkDocs with Material theme and versioned using Mike.

## Prerequisites

1. Install required tools:
```bash
pip install mkdocs mkdocs-material mike
```

## Creating New Documentation Version
Update `chartsVersion` in the `buildSrc/../Config.kt` file if necessary.

### 1. Generate Documentation
Generate the API documentation and JS demo:
```bash
./gradlew generateDocs
```
This task:
- Generates API documentation with Dokka
- Builds and copies JS demo files
- Places files in the correct directories

### 2. Deploy New Version
From the `docs` directory:
```bash
cd docs
mike deploy X.Y.Z latest
```
Replace `X.Y.Z` with your version number (e.g., `2.0.1`).

### 3. Set Default Version (Optional)
If this should be the default version shown to users:
```bash
mike set-default X.Y.Z
```

### 4. Push to GitHub
To deploy to GitHub Pages:
```bash
mike deploy X.Y.Z latest --push
```

## Local Preview

Preview documentation locally:
```bash
# Preview current version
mkdocs build
mkdocs serve

# Preview all versions
mike serve
```

## Documentation Structure

- `src/api/` - Generated Dokka API documentation
- `src/jsdemo/` - Generated JS demo files
- `mkdocs.yml` - MkDocs configuration
- `public-site/` - Generated mkdocs documentation site

## Version Management

Each version maintains its own:
- API documentation
- Navigation structure
- JS demo

The version selector in the documentation header allows users to switch between versions.

## References

- [Dokka](https://kotlin.github.io/dokka/)
- [MkDocs](https://www.mkdocs.org/)
- [MkDocs Material Theme](https://squidfunk.github.io/mkdocs-material/)
- [Mike](https://github.com/jimporter/mike)
