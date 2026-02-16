#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
SOURCE_SCRIPT="${REPO_ROOT}/scripts/publish-docs-static.sh"

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

assert_file_missing() {
  local path="$1"
  local label="$2"
  if [[ ! -e "${path}" ]]; then
    log_pass "${label}"
  else
    log_fail "${label} (unexpected path: ${path})"
  fi
}

assert_command_fails_with() {
  local expected_text="$1"
  local label="$2"
  shift 2

  local output_file
  output_file="$(mktemp)"
  if "$@" >"${output_file}" 2>&1; then
    log_fail "${label} (command succeeded unexpectedly)"
    rm -f "${output_file}"
    return
  fi

  if grep -Fq "${expected_text}" "${output_file}"; then
    log_pass "${label}"
  else
    log_fail "${label} (missing expected text: ${expected_text})"
    echo "---- command output ----" >&2
    cat "${output_file}" >&2
    echo "------------------------" >&2
  fi
  rm -f "${output_file}"
}

create_fixture_repo() {
  local sandbox_root="$1"
  local mode="${2:-release}"
  local remote_repo="${sandbox_root}/remote.git"
  local work_repo="${sandbox_root}/repo"

  git init --bare "${remote_repo}" >/dev/null
  git init "${work_repo}" >/dev/null

  git -C "${work_repo}" config user.name "Test Bot"
  git -C "${work_repo}" config user.email "test@example.com"

  mkdir -p "${work_repo}/scripts"
  cp "${SOURCE_SCRIPT}" "${work_repo}/scripts/publish-docs-static.sh"
  chmod +x "${work_repo}/scripts/publish-docs-static.sh"

  echo "fixture" > "${work_repo}/README.md"
  git -C "${work_repo}" add README.md scripts/publish-docs-static.sh
  git -C "${work_repo}" commit -m "init" >/dev/null
  git -C "${work_repo}" branch -M main
  git -C "${work_repo}" remote add origin "${remote_repo}"
  git -C "${work_repo}" push -u origin main >/dev/null

  git -C "${work_repo}" checkout -b docs-static >/dev/null
  mkdir -p "${work_repo}/docs/static/api/1.2.2"
  mkdir -p "${work_repo}/docs/static/api/1.2.3"
  mkdir -p "${work_repo}/docs/static/api/snapshot"
  mkdir -p "${work_repo}/docs/static/demo/1.2.2"
  mkdir -p "${work_repo}/docs/static/demo/1.2.3"
  mkdir -p "${work_repo}/docs/static/demo/snapshot"
  mkdir -p "${work_repo}/docs/static/playground/1.2.2"
  mkdir -p "${work_repo}/docs/static/playground/1.2.3"
  mkdir -p "${work_repo}/docs/static/playground/snapshot"

  echo "preserve" > "${work_repo}/docs/static/demo/1.2.2/keep.txt"
  echo "preserve" > "${work_repo}/docs/static/api/1.2.2/keep.txt"
  echo "preserve" > "${work_repo}/docs/static/playground/1.2.2/keep.txt"
  echo "stale-current" > "${work_repo}/docs/static/demo/1.2.3/stale.txt"
  echo "stale-current" > "${work_repo}/docs/static/api/1.2.3/stale.txt"
  echo "stale-current" > "${work_repo}/docs/static/playground/1.2.3/stale.txt"
  echo "stale-snapshot" > "${work_repo}/docs/static/demo/snapshot/stale.txt"
  echo "stale-snapshot" > "${work_repo}/docs/static/api/snapshot/stale.txt"
  echo "stale-snapshot" > "${work_repo}/docs/static/playground/snapshot/stale.txt"

  git -C "${work_repo}" add docs/static
  git -C "${work_repo}" commit -m "seed docs-static content" >/dev/null
  git -C "${work_repo}" push -u origin docs-static >/dev/null
  git -C "${work_repo}" checkout main >/dev/null

  rm -rf "${work_repo}/docs/static"
  mkdir -p "${work_repo}/docs/static/api/1.2.3"
  mkdir -p "${work_repo}/docs/static/demo/1.2.3"
  mkdir -p "${work_repo}/docs/static/playground/1.2.3"

  echo "new-current" > "${work_repo}/docs/static/demo/1.2.3/new.txt"
  echo "new-current" > "${work_repo}/docs/static/api/1.2.3/new.txt"
  echo "new-current" > "${work_repo}/docs/static/playground/1.2.3/new.txt"

  if [[ "${mode}" == "release" ]]; then
    # Simulate accidental snapshot changes in a release run; they must not be published.
    mkdir -p "${work_repo}/docs/static/api/snapshot"
    mkdir -p "${work_repo}/docs/static/demo/snapshot"
    mkdir -p "${work_repo}/docs/static/playground/snapshot"
    echo "should-not-publish" > "${work_repo}/docs/static/demo/snapshot/new.txt"
    echo "should-not-publish" > "${work_repo}/docs/static/api/snapshot/new.txt"
    echo "should-not-publish" > "${work_repo}/docs/static/playground/snapshot/new.txt"
  else
    # Snapshot run should fully refresh snapshot.
    mkdir -p "${work_repo}/docs/static/api/snapshot"
    mkdir -p "${work_repo}/docs/static/demo/snapshot"
    mkdir -p "${work_repo}/docs/static/playground/snapshot"
    echo "new-snapshot" > "${work_repo}/docs/static/demo/snapshot/new.txt"
    echo "new-snapshot" > "${work_repo}/docs/static/api/snapshot/new.txt"
    echo "new-snapshot" > "${work_repo}/docs/static/playground/snapshot/new.txt"
  fi

  echo "${work_repo}"
}

test_refuses_local_execution() {
  local sandbox
  sandbox="$(mktemp -d)"
  local repo
  repo="$(create_fixture_repo "${sandbox}" "release")"

  assert_command_fails_with \
    "CI-only and must not be run locally" \
    "rejects local execution when CI is not set" \
    bash -lc "cd '${repo}' && CURRENT_VERSION=1.2.3 bash scripts/publish-docs-static.sh"

  rm -rf "${sandbox}"
}

test_requires_current_version() {
  local sandbox
  sandbox="$(mktemp -d)"
  local repo
  repo="$(create_fixture_repo "${sandbox}" "release")"

  assert_command_fails_with \
    "CURRENT_VERSION is required" \
    "requires CURRENT_VERSION" \
    bash -lc "cd '${repo}' && CI=true bash scripts/publish-docs-static.sh"

  rm -rf "${sandbox}"
}

test_release_refreshes_current_and_preserves_snapshot_history() {
  local sandbox
  sandbox="$(mktemp -d)"
  local repo
  repo="$(create_fixture_repo "${sandbox}" "release")"

  (
    cd "${repo}"
    CI=true CURRENT_VERSION=1.2.3 bash scripts/publish-docs-static.sh >/dev/null
  )

  local inspect_repo="${sandbox}/inspect"
  git clone "${sandbox}/remote.git" "${inspect_repo}" >/dev/null
  git -C "${inspect_repo}" checkout docs-static >/dev/null

  assert_file_exists \
    "${inspect_repo}/docs/static/demo/1.2.2/keep.txt" \
    "preserves older demo version"
  assert_file_exists \
    "${inspect_repo}/docs/static/api/1.2.2/keep.txt" \
    "preserves older api version"
  assert_file_exists \
    "${inspect_repo}/docs/static/playground/1.2.2/keep.txt" \
    "preserves older playground version"

  assert_file_exists \
    "${inspect_repo}/docs/static/demo/1.2.3/new.txt" \
    "refreshes current demo version"
  assert_file_exists \
    "${inspect_repo}/docs/static/api/1.2.3/new.txt" \
    "refreshes current api version"
  assert_file_exists \
    "${inspect_repo}/docs/static/playground/1.2.3/new.txt" \
    "refreshes current playground version"

  assert_file_missing \
    "${inspect_repo}/docs/static/demo/1.2.3/stale.txt" \
    "removes stale file from current demo version"
  assert_file_missing \
    "${inspect_repo}/docs/static/api/1.2.3/stale.txt" \
    "removes stale file from current api version"
  assert_file_missing \
    "${inspect_repo}/docs/static/playground/1.2.3/stale.txt" \
    "removes stale file from current playground version"

  assert_file_exists \
    "${inspect_repo}/docs/static/demo/snapshot/stale.txt" \
    "keeps snapshot demo unchanged on release"
  assert_file_exists \
    "${inspect_repo}/docs/static/api/snapshot/stale.txt" \
    "keeps snapshot api unchanged on release"
  assert_file_exists \
    "${inspect_repo}/docs/static/playground/snapshot/stale.txt" \
    "keeps snapshot playground unchanged on release"

  assert_file_missing \
    "${inspect_repo}/docs/static/demo/snapshot/new.txt" \
    "does not publish snapshot demo changes on release"
  assert_file_missing \
    "${inspect_repo}/docs/static/api/snapshot/new.txt" \
    "does not publish snapshot api changes on release"
  assert_file_missing \
    "${inspect_repo}/docs/static/playground/snapshot/new.txt" \
    "does not publish snapshot playground changes on release"

  rm -rf "${sandbox}"
}

test_snapshot_refreshes_snapshot_fully() {
  local sandbox
  sandbox="$(mktemp -d)"
  local repo
  repo="$(create_fixture_repo "${sandbox}" "snapshot")"

  (
    cd "${repo}"
    CI=true CURRENT_VERSION=1.2.4-SNAPSHOT bash scripts/publish-docs-static.sh >/dev/null
  )

  local inspect_repo="${sandbox}/inspect"
  git clone "${sandbox}/remote.git" "${inspect_repo}" >/dev/null
  git -C "${inspect_repo}" checkout docs-static >/dev/null

  assert_file_exists \
    "${inspect_repo}/docs/static/demo/snapshot/new.txt" \
    "refreshes snapshot demo on snapshot release"
  assert_file_exists \
    "${inspect_repo}/docs/static/api/snapshot/new.txt" \
    "refreshes snapshot api on snapshot release"
  assert_file_exists \
    "${inspect_repo}/docs/static/playground/snapshot/new.txt" \
    "refreshes snapshot playground on snapshot release"

  assert_file_missing \
    "${inspect_repo}/docs/static/demo/snapshot/stale.txt" \
    "removes stale snapshot demo files on snapshot release"
  assert_file_missing \
    "${inspect_repo}/docs/static/api/snapshot/stale.txt" \
    "removes stale snapshot api files on snapshot release"
  assert_file_missing \
    "${inspect_repo}/docs/static/playground/snapshot/stale.txt" \
    "removes stale snapshot playground files on snapshot release"

  rm -rf "${sandbox}"
}

main() {
  test_refuses_local_execution
  test_requires_current_version
  test_release_refreshes_current_and_preserves_snapshot_history
  test_snapshot_refreshes_snapshot_fully

  if [[ "${failures}" -gt 0 ]]; then
    echo "Tests failed: ${failures}" >&2
    exit 1
  fi

  echo "All publish-docs-static behavior tests passed."
}

main "$@"
