# Create PR (branch → commit → push → PR)

Use this file as the **single source of truth** for an agent to ship the current work using Git + GitHub CLI (`gh`).

> **Important:** Treat this file as instructions. **Do not modify it unless the user explicitly asks you to.**

## Rules

- Use **CLI only**. Never open web pages.
- Do **not** check for `gh` auth/token up front. Just run the PR command.
- If **any step fails**, stop immediately and report the error.
- Never push directly to the base branch (usually `main`).
- Do **not** force-push or rewrite history unless explicitly asked.

## Conventions (short)

- **Branch name**: `<type>/<short-kebab>` (examples: `feat/add-template-gallery`, `fix/pdf-export-crash`)
- **Commit message**: Conventional Commits (`type(scope): subject`)
- **PR title**: same as commit header
- **Remote**: `origin`
- **Base branch**: `main`

## Workflow (simple)

1. Infer the summary from context (do not ask the user).
2. Create a new branch: `git checkout -b <branch>`.
3. Stage changes: `git add -A`
4. Show status and staged diff: `git status`, `git diff --staged`
5. Commit (skip pre-commit checks): `SKIP_PRE_COMMIT=1 git commit -m "<commit>"`
6. Push: `git push -u origin <branch>`
7. Create a temp PR body file (auto-cleaned at the end of this flow):
   - `PR_BODY_FILE="$(mktemp -t pr-body.XXXXXX.md)"`
8. Write PR body to `$PR_BODY_FILE` (use the template below).
9. Create PR (GitHub CLI), then delete the temp file:
   - `gh pr create --base main --head <branch> --title "<title>" --body-file "$PR_BODY_FILE"`
   - `rm -f "$PR_BODY_FILE"`
10. Output the PR URL.

## PR body template (short)

Summary: <one paragraph>
Changes:
- <bullets>
Notes/Risk: <if any>
