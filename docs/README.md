# Charts Documentation

A modern documentation site for the Charts library with versioned content, API documentation, and interactive playground.

## Structure

```
docs/
├── docs-app/          # Next.js documentation application
├── content/           # Versioned wiki content (Markdown/MDX)
│   └── v2/
│       └── wiki/      # Wiki pages for v2
├── static/            # Generated static assets
│   ├── api/           # Dokka-generated API docs per version
│   │   └── v2/
│   └── demo/          # Kotlin/JS demo output per version
│       └── v2/
└── registry/
    └── versions.json  # Version registry (source of truth)
```

## Getting Started

### Prerequisites

- Node.js 18+
- npm

### Development

```bash
cd docs-app
npm install
npm run dev
```

The documentation site will be available at `http://localhost:3000`.

### Building

```bash
npm run build
npm run start
```

## Version Registry

The version registry at `registry/versions.json` is the source of truth for all documentation versions. Each version entry includes:

- `id` - Unique version identifier (e.g., "v2")
- `label` - Display label (e.g., "2.x")
- `wikiRoot` - Path to wiki content
- `apiBase` - Path to Dokka API docs
- `demoBase` - Path to JS demo (optional)
- `notes` - Optional release notes

## Adding a New Version

1. Create version content folder: `content/vX/wiki/`
2. Add required pages: `index.md`
3. Generate Dokka output: `static/api/vX/`
4. Generate JS demo (if enabled): `static/demo/vX/`
5. Update `registry/versions.json` with the new version entry
6. Create a PR against `main`

## Content Guidelines

- Wiki pages are plain Markdown (`.md`) or MDX (`.mdx`)
- No required frontmatter (optional `title`, `description`, `order`)
- Navigation order follows file structure
- Include release notes in the version `index.md`

## Deployment

The site is deployed on Vercel after `Publish Docs Static` completes.

## Legacy Versions

Legacy versions (v1, v1.1) include wiki and API docs only. The playground is disabled for legacy versions to avoid rebuilding historical demos.
