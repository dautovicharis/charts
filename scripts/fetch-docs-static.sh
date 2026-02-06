#!/usr/bin/env bash
set -euo pipefail

REMOTE="${DOCS_STATIC_REMOTE:-origin}"
BRANCH="${DOCS_STATIC_BRANCH:-docs-static}"
SKIP="${DOCS_SKIP_STATIC_FETCH:-}"

if [[ "${SKIP}" == "1" || "${SKIP}" == "true" ]]; then
  exit 0
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
STATIC_DIR="${REPO_ROOT}/docs/static"

if [[ -d "${STATIC_DIR}/api" ]]; then
  exit 0
fi

if ! git -C "${REPO_ROOT}" rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "Not a git repository; skipping docs/static fetch." >&2
  exit 0
fi

echo "Fetching docs/static from ${REMOTE}/${BRANCH}..."
git -C "${REPO_ROOT}" fetch "${REMOTE}" "${BRANCH}"
git -C "${REPO_ROOT}" checkout "${REMOTE}/${BRANCH}" -- docs/static
