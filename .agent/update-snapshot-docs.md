# Docs Snapshot Update Workflow

This workflow is for publishing snapshot docs (for example, `2.0.3-SNAPSHOT`).

## Rules
- Generated docs assets live on the orphan branch `docs-static`.
- Do **not** commit `docs/static` to `main`.
- Do **not** bump the version if `chartsVersion` is already a `-SNAPSHOT` value.
- Vercel docs build fetches static assets from `${DOCS_STATIC_REMOTE:-origin}/${DOCS_STATIC_BRANCH:-docs-static}`.

## Workflow
1. Ensure snapshot version:
   - `buildSrc/src/main/kotlin/io/github/dautovicharis/charts/Config.kt`
   - If `chartsVersion` is already `*-SNAPSHOT`, keep it unchanged.
   - Otherwise, bump to the next patch version and set it to `-SNAPSHOT` (unless instructed differently).
   - Example: if current version is `2.0.2`, set `chartsVersion = "2.0.3-SNAPSHOT"`.
2. Update snapshot docs content:
   - `docs/content/snapshot/wiki/**`
3. Generate static assets:
   - `./gradlew generateDocs`
4. Publish to orphan branch:
   - `REMOTE=upstream BRANCH=docs-static scripts/publish-docs-static.sh`
5. Trigger docs redeploy (push to tracked branch or manual redeploy in Vercel).

## Notes
- `docs-static` must be updated via the publish script.
- You need write access to push `upstream/docs-static`.
