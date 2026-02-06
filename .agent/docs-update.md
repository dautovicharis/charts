# Docs Update Workflow

This repo stores generated docs assets (`docs/static/**`) in an orphan branch (`docs-static`).
Do **not** commit `docs/static` to `main`.

## Release Flow (e.g., 2.0.2)
1. Update library version:
   - `buildSrc/src/main/kotlin/io/github/dautovicharis/charts/Config.kt`
     - `chartsVersion = "2.0.2"`
2. Update docs content:
   - `docs/content/2.0.2/wiki/**` (create `index.md` and any pages).
3. Register the version:
   - `docs/registry/versions.json` (new entry with `apiBase` + `demoBase`).
4. Generate static assets:
   - `./gradlew generateDocs`
   - Output should be:
     - `docs/static/api/2.0.2/**`
     - `docs/static/demo/2.0.2/**` (if demo is still published)
5. Publish to orphan branch:
   - `REMOTE=upstream BRANCH=docs-static scripts/publish-docs-static.sh`

## Snapshot Flow (e.g., 2.0.3-SNAPSHOT)
1. Bump snapshot version:
   - `buildSrc/src/main/kotlin/io/github/dautovicharis/charts/Config.kt`
     - `chartsVersion = "2.0.3-SNAPSHOT"`
2. Update snapshot docs:
   - `docs/content/snapshot/wiki/**`
3. Regenerate and publish:
   - `./gradlew generateDocs`
   - `REMOTE=upstream BRANCH=docs-static scripts/publish-docs-static.sh`

## Notes
- `docs/static` is ignored on `main`.
- `docs-static` is an orphan branch; always publish via the script.
- You need contributor (write) access to push updates to `upstream/docs-static`.
- If you’re working from a fork, keep `REMOTE=upstream` so the orphan branch updates upstream. 
