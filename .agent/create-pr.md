# Create PR (branch → commit → push → PR)

Use this file as the **single source of truth** for an agent to ship the current work using Git + GitHub CLI (`gh`).

> **Important:** Treat this file as instructions. **Do not modify it unless the user explicitly asks you to.**

## Rules

- Use **CLI only**. Never open web pages.
- Do **not** check for `gh` auth/token up front. Just run the PR command.
- If **any step fails**, stop immediately and report the error.
- Never push directly to the base branch (usually `main`).
- Do **not** force-push or rewrite history unless explicitly asked.
- Do **not** run docs static publish scripts manually; CI handles docs-static updates after merge to `main`.
- Always run `./gradlew ktlintFormat` before staging/commit.
- If `ktlintFormat` fails, fix the reported issues and re-run `./gradlew ktlintFormat` until it passes.

## Conventions (short)

- **Branch name**: `<type>/<short-kebab>` (examples: `feat/add-template-gallery`, `fix/pdf-export-crash`)
- **Commit message**: Conventional Commits (`type(scope): subject`)
- **PR title**: same as commit header
- **Base remote (PR target)**: use `upstream` if it exists; otherwise `origin`
- **Head remote (push)**: use `origin` if it exists; otherwise `upstream`
- **Upstream repo**: `dautovicharis/charts`
- **Base branch**: `main`
- **PR head format**:
  - If base is `upstream` and your branch is on `origin` (a fork), use `--head <forkOwner>:<branch>`.
  - Otherwise use `--head <branch>`.

## Workflow (simple)

1. Infer the summary from context (do not ask the user).
2. Decide remotes (base vs head) using the conventions above.
3. Create a new branch: `git checkout -b <branch>`.
4. Run formatting/lint flow:
   - `./gradlew ktlintFormat`
   - If it fails, fix the reported issues and run `./gradlew ktlintFormat` again until it succeeds
5. Stage changes: `git add -A`
6. Commit: `git commit -m "<commit>"`
7. Push: `git push -u <head-remote> <branch>`
8. Create a temp PR body file (auto-cleaned at the end of this flow):
   - `PR_BODY_FILE="$(mktemp -t pr-body.XXXXXX.md)"`
9. Write PR body to `$PR_BODY_FILE` (use the template below).
10. Create PR (GitHub CLI), then delete the temp file:
   - Use the upstream repo as the PR target.
   - If head is on the fork (`origin`), include the fork owner:
     - `gh pr create --repo dautovicharis/charts --base main --head <forkOwner>:<branch> --title "<title>" --body-file "$PR_BODY_FILE"`
   - If head is on upstream, use:
     - `gh pr create --repo dautovicharis/charts --base main --head <branch> --title "<title>" --body-file "$PR_BODY_FILE"`
   - `rm -f "$PR_BODY_FILE"`
11. Output the PR URL.

## PR body template (short)

## Summary
<one paragraph>

## Changes
- <bullets>

## Notes/Risk
- <if any; otherwise say "None">
