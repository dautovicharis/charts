#!/usr/bin/env bash
set -euo pipefail

REMOTE="${REMOTE:-origin}"
BRANCH="${BRANCH:-docs-static}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

STATIC_DIR="${REPO_ROOT}/docs/static"

if [[ ! -d "${STATIC_DIR}" ]]; then
  echo "docs/static is missing. Run './gradlew generateDocs' first." >&2
  exit 1
fi

if ! command -v rsync >/dev/null 2>&1; then
  echo "rsync is required to publish docs/static." >&2
  exit 1
fi

git -C "${REPO_ROOT}" fetch "${REMOTE}" "${BRANCH}:${BRANCH}"

WORKTREE="$(mktemp -d)"
cleanup() {
  git -C "${REPO_ROOT}" worktree remove -f "${WORKTREE}" >/dev/null 2>&1 || true
  rm -rf "${WORKTREE}" >/dev/null 2>&1 || true
}
trap cleanup EXIT

git -C "${REPO_ROOT}" worktree add "${WORKTREE}" "${BRANCH}"

mkdir -p "${WORKTREE}/docs/static"
rsync -a --delete "${STATIC_DIR}/" "${WORKTREE}/docs/static/"

git -C "${WORKTREE}" add docs/static

if git -C "${WORKTREE}" diff --cached --quiet; then
  echo "No changes to publish."
  exit 0
fi

git -C "${WORKTREE}" commit -m "docs: update static assets"
git -C "${WORKTREE}" push "${REMOTE}" "${BRANCH}"
