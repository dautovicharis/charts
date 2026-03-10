# Create Changeset

Create a PR changeset file when the PR is user-impacting.

## Required input

- `pr_number` (only when run standalone)

## Inference

- When called from `.agent/create-pr.md`, use current PR context.
- Infer `short_kebab` from PR title or branch name.
- Infer `type` from PR/commit semantics.
- Infer `module` from touched files and project modules.
- Draft `release_note` from user-visible impact.
- Explicit user-provided values are optional overrides.

## User-impact gate

- Skip changeset creation for technical/internal-only PRs (CI/workflows, dependency-only bumps with no behavior change, internal refactors/build cleanup, lint/format/repo maintenance).
- If skipped, output exactly: `No changeset needed (technical/internal-only PR).`

## Steps

1. Apply the user-impact gate.
2. If skipped, stop.
3. Ensure `docs/content/snapshot/changes/` exists.
4. Create `docs/content/snapshot/changes/<pr-number>-<short-kebab>.md` from `.agent/templates/pr-changeset.md.tpl`.
5. Fill inferred values: `type`, `module`, `pr` (`https://github.com/dautovicharis/charts/pull/<number>`), `release_note`.
6. Validate: no placeholders remain and `release_note` is non-empty.
7. Output the created file path.
