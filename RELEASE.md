# Release Process

This document describes the process for releasing new versions of the Charts library.

## Release Types

### Snapshot Releases (Automatic)
- Triggered automatically on every push to `main` branch
- Only publishes if version in `Config.kt` contains `-SNAPSHOT`
- Published to Maven Central Snapshots repository

### Stable Releases (Manual)
- Triggered manually via GitHub Actions
- Version automatically read from `Config.kt`
- Must NOT contain `-SNAPSHOT` suffix
- Published to Maven Central
- Git tag automatically created after successful release

## Stable Release Process

### 1. Create Release Branch

```bash
# Create and checkout release branch
git checkout -b release/X.Y.Z
```

### 2. Update Version and Release Notes

Update `buildSrc/src/main/kotlin/io/github/dautovicharis/charts/Config.kt`:
```kotlin
const val chartsVersion = "X.Y.Z"  // Remove -SNAPSHOT suffix
```

Update release notes in `docs/src/index.md`:
```markdown
## What's New in X.Y.Z

### 🐛 Fixes
- **Description**: Details ([#PR](link))

### ⬆️ Updates
- **Description**: Details ([#PR](link))

### 📚 Documentation
- **Description**: Details ([#PR](link))
```

### 3. Generate and Deploy Documentation

```bash
# Generate documentation
./gradlew generateDocs

# Deploy to GitHub Pages
cd docs
mike deploy X.Y.Z latest
mike set-default X.Y.Z
mike delete X.Y.Z-SNAPSHOT  # Remove old SNAPSHOT version if exists
git push origin gh-pages
cd ..
```

### 4. Commit and Push Release Branch

```bash
git add buildSrc/src/main/kotlin/io/github/dautovicharis/charts/Config.kt docs/
git commit -m "Release version X.Y.Z"
git push origin release/X.Y.Z
```

### 5. Trigger Release Workflow

1. Go to: https://github.com/dautovicharis/charts/actions/workflows/release.yml
2. Click "Run workflow"
3. Select branch: `release/X.Y.Z`
4. Click "Run workflow"

The workflow automatically:
- Reads version from `Config.kt`
- Verifies version is not a SNAPSHOT
- Builds and tests the library
- Publishes to Maven Central
- Creates GitHub Release
- Creates and pushes git tag

### 6. Verify Release

- GitHub Actions workflow completes successfully
- GitHub Release created: https://github.com/dautovicharis/charts/releases
- Git tag created: `git fetch --tags && git tag -l`
- Maven Central publication (may take a few hours): https://central.sonatype.com/artifact/io.github.dautovicharis/charts
- Documentation live: https://dautovicharis.github.io/charts/

### 7. Merge Release Branch to Main

```bash
# Merge release branch to main
git checkout main
git merge release/X.Y.Z
git push origin main

# Delete release branch
git branch -d release/X.Y.Z
git push origin --delete release/X.Y.Z
```

### 8. Prepare for Next Development

```bash
# Create branch for next development iteration
git checkout -b prepare-next-dev

# Update Config.kt
const val chartsVersion = "X.Y.Z+1-SNAPSHOT"

# Commit and push
git add buildSrc/src/main/kotlin/io/github/dautovicharis/charts/Config.kt
git commit -m "Prepare for next development iteration"
git push origin prepare-next-dev

# Create PR and merge to main
```

## Quick Reference

### Release Checklist
- [ ] Create release branch `release/X.Y.Z`
- [ ] Update version in `Config.kt` (remove `-SNAPSHOT`)
- [ ] Update release notes in `docs/src/index.md`
- [ ] Generate and deploy documentation
- [ ] Commit and push release branch
- [ ] Trigger release workflow from release branch
- [ ] Verify release (GitHub Release, git tag, Maven Central)
- [ ] Merge release branch to main
- [ ] Prepare for next development iteration

### Common Commands
```bash
# Create release branch
git checkout -b release/X.Y.Z

# Generate and deploy documentation
./gradlew generateDocs
cd docs && mike deploy X.Y.Z latest && mike set-default X.Y.Z && git push origin gh-pages && cd ..

# Commit and push release branch
git add . && git commit -m "Release version X.Y.Z" && git push origin release/X.Y.Z

# Trigger release from release branch: https://github.com/dautovicharis/charts/actions/workflows/release.yml

# After successful release, merge to main
git checkout main && git merge release/X.Y.Z && git push origin main
git branch -d release/X.Y.Z && git push origin --delete release/X.Y.Z
```

## Troubleshooting

**SNAPSHOT Version Error**
- Ensure `Config.kt` version does NOT contain `-SNAPSHOT` suffix
- Update version and re-run workflow

**Maven Central Publication Failed**
- Check GitHub repository secrets are configured
- Verify GPG key is valid and not expired
- Review workflow logs for errors

**Documentation Not Updated**
- Ensure `./gradlew generateDocs` completed successfully
- Verify changes were pushed to `gh-pages` branch
- GitHub Pages may take a few minutes to update

## Required GitHub Secrets

Configure these in repository settings:
- `MAVEN_CENTRAL_USERNAME`
- `MAVEN_CENTRAL_PASSWORD`
- `SIGNING_KEY_ID`
- `SIGNING_PASSWORD`
- `SIGNING_SECRET_KEY_RING_FILE` (Base64-encoded)

