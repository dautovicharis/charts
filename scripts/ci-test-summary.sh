#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEMPLATES_DIR="${SCRIPT_DIR}/templates"

SUMMARY_FILE="${CI_TEST_SUMMARY_FILE:-ci-test-summary.md}"
SUMMARY_JSON_FILE="${CI_TEST_SUMMARY_JSON_FILE:-ci-test-summary.json}"
SUMMARY_TEMPLATE_MD="${CI_TEST_SUMMARY_TEMPLATE_MD:-${TEMPLATES_DIR}/ci-test-summary.md.tpl}"
SUMMARY_TEMPLATE_JSON="${CI_TEST_SUMMARY_TEMPLATE_JSON:-${TEMPLATES_DIR}/ci-test-summary.json.tpl}"
FORCE_ZERO="${CI_TEST_SUMMARY_FORCE_ZERO:-false}"
SKIP_STEP_SUMMARY="${CI_TEST_SUMMARY_SKIP_STEP_SUMMARY:-false}"
GRADLE_TEST_OUTCOME="${CI_GRADLE_TEST_OUTCOME:-}"
SHOULD_RUN_TESTS="${CI_SHOULD_RUN:-true}"

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

line_text() {
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

  printf -- '- %s %s: %s' "${icon}" "${label}" "${text}"
}

render_template() {
  local template_path="$1"
  local output_path="$2"
  shift 2

  if [[ ! -f "${template_path}" ]]; then
    echo "Missing template file: ${template_path}" >&2
    return 1
  fi

  local content
  content="$(cat "${template_path}")"

  while (($# > 0)); do
    local key="$1"
    local value="$2"
    shift 2

    content="${content//\{\{${key}\}\}/${value}}"
  done

  printf '%s\n' "${content}" > "${output_path}"
}

main() {
  local charts_tests=0 charts_failures=0 charts_errors=0 charts_skipped=0
  local app_tests=0 app_failures=0 app_errors=0 app_skipped=0
  local playground_tests=0 playground_failures=0 playground_errors=0 playground_skipped=0
  local android_screenshot_tests=0 android_screenshot_failures=0 android_screenshot_errors=0 android_screenshot_skipped=0

  if [[ "${FORCE_ZERO}" != "true" ]]; then
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

    read -r app_tests app_failures app_errors app_skipped < <(
      collect_counts_for_dirs app/build/test-results/jvmTest
    )

    read -r playground_tests playground_failures playground_errors playground_skipped < <(
      collect_counts_for_dirs \
        playground/build/test-results/jvmTest \
        playground/build/test-results/jsBrowserTest
    )

    read -r android_screenshot_tests android_screenshot_failures android_screenshot_errors android_screenshot_skipped < <(
      collect_counts_for_dirs androidApp/build/test-results/validateDebugScreenshotTest
    )
  fi

  local behavior_tests=0
  local behavior_failures=0
  local behavior_errors=0
  if [[ "${FORCE_ZERO}" != "true" ]]; then
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
  fi

  local total_tests total_failures total_errors total_broken gradle_step_broken effective_total_broken
  total_tests=$((charts_tests + app_tests + playground_tests + android_screenshot_tests + behavior_tests))
  total_failures=$((charts_failures + app_failures + playground_failures + android_screenshot_failures + behavior_failures))
  total_errors=$((charts_errors + app_errors + playground_errors + android_screenshot_errors + behavior_errors))
  total_broken=$((total_failures + total_errors))
  gradle_step_broken=0

  if [[ "${FORCE_ZERO}" != "true" && "${SHOULD_RUN_TESTS}" == "true" ]]; then
    case "${GRADLE_TEST_OUTCOME}" in
      failure|cancelled|timed_out|action_required)
        gradle_step_broken=1
        ;;
      *)
        ;;
    esac
  fi
  effective_total_broken=$((total_broken + gradle_step_broken))

  local charts_line playground_line android_screenshot_line ci_behavior_line total_line total_test_word
  charts_line="$(line_text "Charts" "${charts_tests}" "${charts_failures}" "${charts_errors}")"
  playground_line="$(line_text "Playground" "${playground_tests}" "${playground_failures}" "${playground_errors}")"
  android_screenshot_line="$(line_text "Android screenshot" "${android_screenshot_tests}" "${android_screenshot_failures}" "${android_screenshot_errors}")"
  ci_behavior_line="$(line_text "CI behavior" "${behavior_tests}" "${behavior_failures}" "${behavior_errors}")"

  total_test_word="tests"
  if ((total_tests == 1)); then
    total_test_word="test"
  fi
  if ((effective_total_broken == 0)); then
    total_line="- ✅ Total: ${total_tests} ${total_test_word} completed successfully"
  elif ((total_broken == 0 && gradle_step_broken == 1)); then
    total_line="- ❌ Total: test workflow failed before complete test results were produced"
  else
    total_line="- ❌ Total: ${total_tests} ${total_test_word} completed, ${effective_total_broken} failed"
  fi

  render_template \
    "${SUMMARY_TEMPLATE_MD}" \
    "${SUMMARY_FILE}" \
    charts_line "${charts_line}" \
    playground_line "${playground_line}" \
    android_screenshot_line "${android_screenshot_line}" \
    ci_behavior_line "${ci_behavior_line}" \
    total_line "${total_line}"

  render_template \
    "${SUMMARY_TEMPLATE_JSON}" \
    "${SUMMARY_JSON_FILE}" \
    charts_tests "${charts_tests}" \
    charts_failures "${charts_failures}" \
    charts_errors "${charts_errors}" \
    app_tests "${app_tests}" \
    app_failures "${app_failures}" \
    app_errors "${app_errors}" \
    playground_tests "${playground_tests}" \
    playground_failures "${playground_failures}" \
    playground_errors "${playground_errors}" \
    android_screenshot_tests "${android_screenshot_tests}" \
    android_screenshot_failures "${android_screenshot_failures}" \
    android_screenshot_errors "${android_screenshot_errors}" \
    ci_behavior_tests "${behavior_tests}" \
    ci_behavior_failures "${behavior_failures}" \
    ci_behavior_errors "${behavior_errors}" \
    total_tests "${total_tests}" \
    total_failures "$((total_failures + gradle_step_broken))" \
    total_errors "${total_errors}"

  if [[ "${SKIP_STEP_SUMMARY}" != "true" && -n "${GITHUB_STEP_SUMMARY:-}" ]]; then
    cat "${SUMMARY_FILE}" >> "${GITHUB_STEP_SUMMARY}"
  fi

  cat "${SUMMARY_FILE}"
}

main "$@"
