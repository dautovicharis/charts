#!/usr/bin/env bash
set -euo pipefail

SUMMARY_FILE="${API_COMPAT_SUMMARY_FILE:-api-summary/api-compatibility-summary.md}"
BASELINE_VERSION="${API_COMPAT_BASELINE_VERSION:-}"
EXIT_CODE="${API_COMPAT_EXIT_CODE:-}"
RESULT="${API_COMPAT_RESULT:-}"
BINARY_CHANGE_DETECTED="${API_COMPAT_BINARY_CHANGE_DETECTED:-false}"
HAS_BREAKING_LABEL="${API_COMPAT_HAS_BREAKING_LABEL:-false}"
RUN_URL="${API_COMPAT_RUN_URL:-}"

label_state="missing"
if [[ "${HAS_BREAKING_LABEL}" == "true" ]]; then
  label_state="present"
fi

binary_break_state="no"
if [[ "${BINARY_CHANGE_DETECTED}" == "true" ]]; then
  binary_break_state="yes"
fi

mkdir -p "$(dirname "${SUMMARY_FILE}")"
{
  echo "## API Compatibility"
  echo "- Result: **${RESULT}**"
  echo "- Baseline: \`${BASELINE_VERSION}\`"
  echo "- Exit code: \`${EXIT_CODE}\`"
  echo "- breaking-change label: ${label_state}"
  echo "- Binary break detected: ${binary_break_state}"
  echo "- Run: ${RUN_URL}"
} > "${SUMMARY_FILE}"
