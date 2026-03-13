# Docs Release Publish

Workflow: `charts/.github/workflows/publish-docs-release.yml`

```mermaid
flowchart TD
  A["workflow_dispatch (allow_overwrite_release)"] --> B["Checkout main + AWS OIDC creds"]
  B --> C["axion: resolve-release-version.sh"]
  C --> D["Gradle (Charts): generateDocs"]
  D --> E{"Required release files exist?"}
  E -- No --> EX["Fail"]
  E -- Yes --> F["Checkout charts-docs"]
  F --> G{"Version exists in charts-docs registry?"}
  G -- No --> GX["Fail"]
  G -- Yes --> H["Run docs link contract check"]
  H --> I["publish-docs-to-s3.sh release"]
  I --> J{"Published?"}
  J -- No --> JX["Stop"]
  J -- Yes --> K["invalidate-docs-cloudfront.sh release"]
```

Required release files:
- `docs/static/api/{release_version}/index.html`
- `docs/static/demo/{release_version}/index.html`
