# Docs Release Update Workflow

This workflow is for publishing a stable docs release (for example, `2.0.2`).

## Rules
- Generated docs assets live on the orphan branch `docs-static`.
- Do **not** commit `docs/static` to `main`.

## Workflow
1. Update library version:
   - `buildSrc/src/main/kotlin/io/github/dautovicharis/charts/Config.kt`
   - Default to a **patch** bump unless explicitly instructed to use minor/major.
   - `chartsVersion = "2.0.2"`
2. Update docs content:
   - `docs/content/2.0.2/wiki/**`
   - Ensure `index.md` exists.
3. Register the new version:
   - Add an entry in `docs/registry/versions.json` with `apiBase` and `demoBase`.
4. Generate static assets:
   - `./gradlew generateDocs`
5. Publish to orphan branch:
   - `REMOTE=upstream BRANCH=docs-static scripts/publish-docs-static.sh`

## Notes
- `docs-static` must be updated via the publish script.
- You need write access to push `upstream/docs-static`.
- If working from a fork, keep `REMOTE=upstream`.
