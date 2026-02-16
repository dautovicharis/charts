#!/usr/bin/env bash
set -euo pipefail

REMOTE="${REMOTE:-origin}"
BRANCH="${BRANCH:-docs-static}"
CURRENT_VERSION="${CURRENT_VERSION:-}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

STATIC_DIR="${REPO_ROOT}/docs/static"

if [[ "${CI:-}" != "true" ]]; then
  echo "This script is CI-only and must not be run locally." >&2
  exit 1
fi

if [[ -z "${CURRENT_VERSION}" ]]; then
  echo "CURRENT_VERSION is required (for targeted refresh of the current docs version)." >&2
  exit 1
fi

if [[ "${CURRENT_VERSION}" == *"/"* || "${CURRENT_VERSION}" == *".."* ]]; then
  echo "CURRENT_VERSION contains invalid path characters: ${CURRENT_VERSION}" >&2
  exit 1
fi

IS_SNAPSHOT_VERSION="false"
if [[ "${CURRENT_VERSION}" == *"-SNAPSHOT" ]]; then
  IS_SNAPSHOT_VERSION="true"
fi

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

refresh_subdir() {
  local rel_path="$1"
  local src="${STATIC_DIR}/${rel_path}"
  local dst="${WORKTREE}/docs/static/${rel_path}"

  if [[ ! -d "${src}" ]]; then
    return 0
  fi

  # Current version and snapshot must be exact copies, including file removals.
  rm -rf "${dst}"
  mkdir -p "${dst}"
  rsync -a "${src}/" "${dst}/"
}

if [[ "${IS_SNAPSHOT_VERSION}" == "true" ]]; then
  refresh_subdir "api/snapshot"
  refresh_subdir "demo/snapshot"
  refresh_subdir "playground/snapshot"
fi

refresh_subdir "api/${CURRENT_VERSION}"
refresh_subdir "demo/${CURRENT_VERSION}"
refresh_subdir "playground/${CURRENT_VERSION}"

if [[ "${IS_SNAPSHOT_VERSION}" == "true" ]]; then
  rsync -a "${STATIC_DIR}/" "${WORKTREE}/docs/static/"
else
  rsync -a \
    --exclude "api/snapshot/" \
    --exclude "demo/snapshot/" \
    --exclude "playground/snapshot/" \
    "${STATIC_DIR}/" "${WORKTREE}/docs/static/"
fi

git -C "${WORKTREE}" add docs/static

if git -C "${WORKTREE}" diff --cached --quiet; then
  echo "No changes to publish."
  exit 0
fi

git -C "${WORKTREE}" commit -m "docs: update static assets"
git -C "${WORKTREE}" push "${REMOTE}" "${BRANCH}"
