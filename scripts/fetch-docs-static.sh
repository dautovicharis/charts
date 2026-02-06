#!/usr/bin/env bash
set -euo pipefail

REMOTE="${DOCS_STATIC_REMOTE:-origin}"
REMOTE_URL="${DOCS_STATIC_REMOTE_URL:-}"
BRANCH="${DOCS_STATIC_BRANCH:-docs-static}"
SKIP="${DOCS_SKIP_STATIC_FETCH:-}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
STATIC_DIR="${REPO_ROOT}/docs/static"
CONTENT_DIR="${REPO_ROOT}/docs/content"
DOCS_APP_PUBLIC_DIR="${REPO_ROOT}/docs/docs-app/public"

sync_public_assets() {
  if [[ "${VERCEL:-}" != "1" && "${CI:-}" != "true" ]]; then
    return 0
  fi

  mkdir -p "${DOCS_APP_PUBLIC_DIR}"

  # Materialize real directories. Symlinks in public are not reliably deployed.
  rm -rf "${DOCS_APP_PUBLIC_DIR}/content"
  cp -a "${CONTENT_DIR}" "${DOCS_APP_PUBLIC_DIR}/content"

  rm -rf "${DOCS_APP_PUBLIC_DIR}/static"
  if [[ -d "${STATIC_DIR}" ]]; then
    cp -a "${STATIC_DIR}" "${DOCS_APP_PUBLIC_DIR}/static"
  fi
}

if [[ "${SKIP}" == "1" || "${SKIP}" == "true" ]]; then
  sync_public_assets
  exit 0
fi

if [[ -d "${STATIC_DIR}/api" ]]; then
  sync_public_assets
  exit 0
fi

if ! git -C "${REPO_ROOT}" rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "Not a git repository; skipping docs/static fetch." >&2
  sync_public_assets
  exit 0
fi

infer_remote_url() {
  if [[ -n "${VERCEL_GIT_REPO_OWNER:-}" && -n "${VERCEL_GIT_REPO_SLUG:-}" ]]; then
    case "${VERCEL_GIT_PROVIDER:-}" in
      github)
        echo "https://github.com/${VERCEL_GIT_REPO_OWNER}/${VERCEL_GIT_REPO_SLUG}.git"
        return 0
        ;;
      gitlab)
        echo "https://gitlab.com/${VERCEL_GIT_REPO_OWNER}/${VERCEL_GIT_REPO_SLUG}.git"
        return 0
        ;;
      bitbucket)
        echo "https://bitbucket.org/${VERCEL_GIT_REPO_OWNER}/${VERCEL_GIT_REPO_SLUG}.git"
        return 0
        ;;
    esac
  fi

  if [[ -n "${GITHUB_REPOSITORY:-}" ]]; then
    echo "https://github.com/${GITHUB_REPOSITORY}.git"
    return 0
  fi

  if [[ -n "${CI_PROJECT_URL:-}" ]]; then
    echo "${CI_PROJECT_URL}"
    return 0
  fi

  return 1
}

if ! git -C "${REPO_ROOT}" remote get-url "${REMOTE}" >/dev/null 2>&1; then
  if [[ -z "${REMOTE_URL}" ]]; then
    REMOTE_URL="$(infer_remote_url || true)"
  fi

  if [[ -n "${REMOTE_URL}" ]]; then
    echo "Adding git remote ${REMOTE} -> ${REMOTE_URL}..."
    git -C "${REPO_ROOT}" remote add "${REMOTE}" "${REMOTE_URL}"
  else
    echo "Remote ${REMOTE} not configured; skipping docs/static fetch." >&2
    sync_public_assets
    exit 0
  fi
fi

echo "Fetching docs/static from ${REMOTE}/${BRANCH}..."
git -C "${REPO_ROOT}" fetch "${REMOTE}" "${BRANCH}"
git -C "${REPO_ROOT}" checkout "${REMOTE}/${BRANCH}" -- docs/static
sync_public_assets
