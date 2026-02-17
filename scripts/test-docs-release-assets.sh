#!/usr/bin/env bash
set -euo pipefail

# Validates docs release readiness for publish workflow:
# - runs link contract checks
# - verifies versioned docs/static API and demo index assets exist
# Intended for post-build docs publishing, not PR-only validation.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

REGISTRY_PATH="${REPO_ROOT}/docs/registry/versions.json"
ASSET_ROOT="${DOCS_RELEASE_LINKS_ASSET_ROOT:-${REPO_ROOT}/docs/static}"

failures=0

log_pass() {
  echo "PASS: $1"
}

log_fail() {
  echo "FAIL: $1" >&2
  failures=$((failures + 1))
}

assert_file_exists() {
  local path="$1"
  local label="$2"
  if [[ -f "${path}" ]]; then
    log_pass "${label}"
  else
    log_fail "${label} (missing: ${path})"
  fi
}

test_release_assets_exist() {
  if [[ ! -f "${REGISTRY_PATH}" ]]; then
    log_fail "versions registry exists (missing: ${REGISTRY_PATH})"
    return
  fi

  local row_count=0
  while IFS=$'\t' read -r id; do
    row_count=$((row_count + 1))

    assert_file_exists "${ASSET_ROOT}/api/${id}/index.html" "api index exists for ${id}"
    assert_file_exists "${ASSET_ROOT}/demo/${id}/index.html" "demo index exists for ${id}"
  done < <(
    node -e '
      const fs = require("fs");
      const registryPath = process.argv[1];
      const parsed = JSON.parse(fs.readFileSync(registryPath, "utf8"));
      if (!Array.isArray(parsed.versions)) process.exit(2);
      for (const version of parsed.versions) {
        const id = version.id ?? "";
        process.stdout.write(id + "\n");
      }
    ' "${REGISTRY_PATH}"
  )

  if ((row_count == 0)); then
    log_fail "versions registry contains at least one release entry"
  else
    log_pass "validated static assets for ${row_count} release entries"
  fi
}

main() {
  bash "${SCRIPT_DIR}/test-docs-release-links-contract.sh"
  test_release_assets_exist

  if ((failures > 0)); then
    echo "Docs release publish validation failed: ${failures} check(s)." >&2
    exit 1
  fi

  echo "All docs release publish checks passed."
}

main "$@"
