#!/usr/bin/env bash
set -euo pipefail

SUMMARY_FILE="${CI_TEST_SUMMARY_FILE:-.ci-test-summary.md}"
SUMMARY_JSON_FILE="${CI_TEST_SUMMARY_JSON_FILE:-.ci-test-summary.json}"

extract_attr() {
  local line="$1"
  local attr="$2"

  if [[ "$line" =~ ${attr}=\"([0-9]+)\" ]]; then
    echo "${BASH_REMATCH[1]}"
  else
    echo "0"
  fi
}

collect_counts_for_dirs() {
  local tests=0
  local failures=0
  local errors=0
  local skipped=0
  local dir

  for dir in "$@"; do
    if [[ ! -d "${dir}" ]]; then
      continue
    fi

    local file
    while IFS= read -r -d '' file; do
      local suite_line
      suite_line="$(grep -m1 '<testsuite ' "${file}" || true)"
      if [[ -z "${suite_line}" ]]; then
        continue
      fi

      tests=$((tests + $(extract_attr "${suite_line}" "tests")))
      failures=$((failures + $(extract_attr "${suite_line}" "failures")))
      errors=$((errors + $(extract_attr "${suite_line}" "errors")))
      skipped=$((skipped + $(extract_attr "${suite_line}" "skipped")))
    done < <(find "${dir}" -type f -name 'TEST-*.xml' -print0)
  done

  printf '%s %s %s %s\n' "${tests}" "${failures}" "${errors}" "${skipped}"
}

render_line() {
  local label="$1"
  local tests="$2"
  local failures="$3"
  local errors="$4"

  local broken=$((failures + errors))
  local icon="✅"
  local test_word="tests"
  local text

  if ((tests == 1)); then
    test_word="test"
  fi

  text="${tests} ${test_word} completed"

  if ((tests == 0)); then
    icon="⚪"
    text="0 tests completed"
  elif ((broken > 0)); then
    icon="❌"
    text="${tests} ${test_word} completed (${broken} failed)"
  fi

  printf -- '- %s %s: %s\n' "${icon}" "${label}" "${text}"
}

main() {
  local charts_tests charts_failures charts_errors charts_skipped
  read -r charts_tests charts_failures charts_errors charts_skipped < <(
    collect_counts_for_dirs \
      charts/build/test-results/jvmTest \
      charts-core/build/test-results/jvmTest \
      charts-line/build/test-results/jvmTest \
      charts-pie/build/test-results/jvmTest \
      charts-bar/build/test-results/jvmTest \
      charts-stacked-bar/build/test-results/jvmTest \
      charts-stacked-area/build/test-results/jvmTest \
      charts-radar/build/test-results/jvmTest
  )

  local app_tests app_failures app_errors app_skipped
  read -r app_tests app_failures app_errors app_skipped < <(
    collect_counts_for_dirs app/build/test-results/jvmTest
  )

  local playground_tests playground_failures playground_errors playground_skipped
  read -r playground_tests playground_failures playground_errors playground_skipped < <(
    collect_counts_for_dirs playground/build/test-results/jvmTest
  )

  local android_screenshot_tests android_screenshot_failures android_screenshot_errors android_screenshot_skipped
  read -r android_screenshot_tests android_screenshot_failures android_screenshot_errors android_screenshot_skipped < <(
    collect_counts_for_dirs androidApp/build/test-results/validateDebugScreenshotTest
  )

  local behavior_tests=0
  local behavior_failures=0
  local behavior_errors=0
  local behavior_outcome="${CI_BEHAVIOR_OUTCOME:-success}"
  case "${behavior_outcome}" in
    success)
      behavior_tests=1
      ;;
    failure|cancelled|timed_out|action_required)
      behavior_tests=1
      behavior_failures=1
      ;;
    *)
      behavior_tests=0
      ;;
  esac

  local total_tests total_failures total_errors total_broken
  total_tests=$((charts_tests + app_tests + playground_tests + android_screenshot_tests + behavior_tests))
  total_failures=$((charts_failures + app_failures + playground_failures + android_screenshot_failures + behavior_failures))
  total_errors=$((charts_errors + app_errors + playground_errors + android_screenshot_errors + behavior_errors))
  total_broken=$((total_failures + total_errors))

  {
    echo "## ✅ Test Summary"
    echo
    render_line "Charts" "${charts_tests}" "${charts_failures}" "${charts_errors}"
    render_line "App" "${app_tests}" "${app_failures}" "${app_errors}"
    render_line "Playground" "${playground_tests}" "${playground_failures}" "${playground_errors}"
    render_line "Android screenshot" "${android_screenshot_tests}" "${android_screenshot_failures}" "${android_screenshot_errors}"
    render_line "CI behavior" "${behavior_tests}" "${behavior_failures}" "${behavior_errors}"
    local total_test_word="tests"
    if ((total_tests == 1)); then
      total_test_word="test"
    fi
    if ((total_broken == 0)); then
      echo "- ✅ Total: ${total_tests} ${total_test_word} completed successfully"
    else
      echo "- ❌ Total: ${total_tests} ${total_test_word} completed, ${total_broken} failed"
    fi
  } > "${SUMMARY_FILE}"

  cat > "${SUMMARY_JSON_FILE}" <<EOF
{"charts":{"tests":${charts_tests},"failures":${charts_failures},"errors":${charts_errors}},"app":{"tests":${app_tests},"failures":${app_failures},"errors":${app_errors}},"playground":{"tests":${playground_tests},"failures":${playground_failures},"errors":${playground_errors}},"android_screenshot":{"tests":${android_screenshot_tests},"failures":${android_screenshot_failures},"errors":${android_screenshot_errors}},"ci_behavior":{"tests":${behavior_tests},"failures":${behavior_failures},"errors":${behavior_errors}},"total":{"tests":${total_tests},"failures":${total_failures},"errors":${total_errors}}}
EOF

  if [[ -n "${GITHUB_STEP_SUMMARY:-}" ]]; then
    cat "${SUMMARY_FILE}" >> "${GITHUB_STEP_SUMMARY}"
  fi

  cat "${SUMMARY_FILE}"
}

main "$@"
