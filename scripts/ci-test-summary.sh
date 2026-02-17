#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
TEMPLATES_DIR="${SCRIPT_DIR}/templates"

SUMMARY_FILE="${CI_TEST_SUMMARY_FILE:-ci-test-summary.md}"
SUMMARY_JSON_FILE="${CI_TEST_SUMMARY_JSON_FILE:-ci-test-summary.json}"
SUMMARY_TEMPLATE_MD="${CI_TEST_SUMMARY_TEMPLATE_MD:-${TEMPLATES_DIR}/ci-test-summary.md.tpl}"
SUMMARY_TEMPLATE_JSON="${CI_TEST_SUMMARY_TEMPLATE_JSON:-${TEMPLATES_DIR}/ci-test-summary.json.tpl}"

FORCE_ZERO="${CI_TEST_SUMMARY_FORCE_ZERO:-false}"
SKIP_STEP_SUMMARY="${CI_TEST_SUMMARY_SKIP_STEP_SUMMARY:-false}"
GRADLE_TEST_OUTCOME="${CI_GRADLE_TEST_OUTCOME:-}"
SHOULD_RUN_TESTS="${CI_SHOULD_RUN:-true}"

CHARTS_RESULT_DIRS=(
  charts/build/test-results/jvmTest
  charts-core/build/test-results/jvmTest
  charts-line/build/test-results/jvmTest
  charts-pie/build/test-results/jvmTest
  charts-bar/build/test-results/jvmTest
  charts-stacked-bar/build/test-results/jvmTest
  charts-stacked-area/build/test-results/jvmTest
  charts-radar/build/test-results/jvmTest
)
APP_RESULT_DIRS=(app/build/test-results/jvmTest)
PLAYGROUND_RESULT_DIRS=(
  playground/build/test-results/jvmTest
  playground/build/test-results/jsBrowserTest
)
ANDROID_SCREENSHOT_RESULT_DIRS=(androidApp/build/test-results/validateDebugScreenshotTest)

test_word() {
  if (($1 == 1)); then
    printf 'test'
  else
    printf 'tests'
  fi
}

# Parse JUnit XML and return: tests failures errors skipped
# If an aggregate <testsuites ...> node is present, use it to avoid double-counting nested suites.
sum_junit_xml_file() {
  local file="$1"
  awk '
    function attr(name, s,    r, has) {
      r = name "=\"[0-9]+\""
      has = match(s, r)
      if (has) {
        sub(".*" name "=\"", "", s)
        sub("\".*", "", s)
        return s + 0
      }
      return 0
    }
    function skipped_like(s,    skipped_val, disabled_val) {
      skipped_val = attr("skipped", s)
      disabled_val = attr("disabled", s)
      return skipped_val + disabled_val
    }

    /<testsuites([[:space:]]|>)/ && !aggregate_seen {
      aggregate_seen = 1
      aggregate_tests = attr("tests", $0)
      aggregate_failures = attr("failures", $0)
      aggregate_errors = attr("errors", $0)
      aggregate_skipped = skipped_like($0)
    }

    /<testsuite([[:space:]]|>)/ {
      suite_tests += attr("tests", $0)
      suite_failures += attr("failures", $0)
      suite_errors += attr("errors", $0)
      suite_skipped += skipped_like($0)
    }

    END {
      if (aggregate_seen) {
        printf "%d %d %d %d\n", aggregate_tests + 0, aggregate_failures + 0, aggregate_errors + 0, aggregate_skipped + 0
      } else {
        printf "%d %d %d %d\n", suite_tests + 0, suite_failures + 0, suite_errors + 0, suite_skipped + 0
      }
    }
  ' "$file"
}

collect_counts_for_dirs() {
  local tests=0 failures=0 errors=0 skipped=0
  local dir file
  local t f e s

  for dir in "$@"; do
    [[ -d "$dir" ]] || continue

    while IFS= read -r -d '' file; do
      if ! read -r t f e s < <(sum_junit_xml_file "$file" 2>/dev/null); then
        continue
      fi
      tests=$((tests + t))
      failures=$((failures + f))
      errors=$((errors + e))
      skipped=$((skipped + s))
    done < <(find "$dir" -type f -name 'TEST-*.xml' -print0)
  done

  printf '%s %s %s %s\n' "$tests" "$failures" "$errors" "$skipped"
}

collect_suite_counts() {
  if [[ "$FORCE_ZERO" == "true" ]]; then
    printf '0 0 0 0\n'
    return
  fi
  collect_counts_for_dirs "$@"
}

line_text() {
  local label="$1" tests="$2" failures="$3" errors="$4" skipped="${5:-0}"
  local broken=$((failures + errors))
  local icon="✅"
  local word text

  word="$(test_word "$tests")"
  text="${tests} ${word} completed"

  if ((tests == 0)); then
    icon="⚪"
    text="0 ${word} completed"
  elif ((broken > 0)); then
    icon="❌"
    if ((skipped > 0)); then
      text="${tests} ${word} completed (${broken} failed, ${skipped} skipped)"
    else
      text="${tests} ${word} completed (${broken} failed)"
    fi
  elif ((skipped > 0)); then
    text="${tests} ${word} completed (${skipped} skipped)"
  fi

  printf -- '- %s %s: %s' "$icon" "$label" "$text"
}

counts_for_step_outcome() {
  local outcome="$1"
  case "$outcome" in
    success)
      printf '1 0\n'
      ;;
    failure|cancelled|timed_out|action_required)
      printf '1 1\n'
      ;;
    *)
      printf '0 0\n'
      ;;
  esac
}

collect_behavior_counts() {
  local tests=0 failures=0 errors=0

  if [[ "$FORCE_ZERO" != "true" ]]; then
    local publish_docs_static_outcome="${CI_BEHAVIOR_PUBLISH_DOCS_STATIC_OUTCOME:-}"
    local docs_release_links_outcome="${CI_BEHAVIOR_DOCS_RELEASE_LINKS_OUTCOME:-}"
    local t1 f1 t2 f2
    read -r t1 f1 < <(counts_for_step_outcome "$publish_docs_static_outcome")
    read -r t2 f2 < <(counts_for_step_outcome "$docs_release_links_outcome")
    tests=$((t1 + t2))
    failures=$((f1 + f2))
  fi

  printf '%s %s %s\n' "$tests" "$failures" "$errors"
}

gradle_step_broken() {
  if [[ "$FORCE_ZERO" == "true" || "$SHOULD_RUN_TESTS" != "true" ]]; then
    printf '0\n'
    return
  fi

  case "$GRADLE_TEST_OUTCOME" in
    failure|cancelled|timed_out|action_required)
      printf '1\n'
      ;;
    *)
      printf '0\n'
      ;;
  esac
}

total_line_text() {
  local total_tests="$1" total_failures="$2" total_errors="$3" total_skipped="$4" gradle_broken="$5"
  local total_broken=$((total_failures + total_errors))
  local effective_broken=$((total_broken + gradle_broken))
  local word
  word="$(test_word "$total_tests")"

  if ((effective_broken == 0)); then
    if ((total_skipped > 0)); then
      printf -- '- ✅ Total: %s %s completed successfully (%s skipped)' "$total_tests" "$word" "$total_skipped"
    else
      printf -- '- ✅ Total: %s %s completed successfully' "$total_tests" "$word"
    fi
    return
  fi

  if ((total_broken == 0 && gradle_broken == 1)); then
    printf -- '- ❌ Total: test workflow failed before complete test results were produced'
    return
  fi

  if ((total_skipped > 0)); then
    printf -- '- ❌ Total: %s %s completed, %s failed (%s skipped)' "$total_tests" "$word" "$effective_broken" "$total_skipped"
  else
    printf -- '- ❌ Total: %s %s completed, %s failed' "$total_tests" "$word" "$effective_broken"
  fi
}

render_template() {
  local template_path="$1" output_path="$2"
  shift 2

  [[ -f "$template_path" ]] || {
    echo "Missing template file: $template_path" >&2
    return 1
  }

  local content
  content="$(cat "$template_path")"

  while (($# > 0)); do
    local key="$1" value="$2"
    shift 2

    if [[ ! "$key" =~ ^[a-zA-Z0-9_]+$ ]]; then
      echo "Invalid template key: $key" >&2
      return 1
    fi
    if [[ "$value" == *$'\n'* ]]; then
      echo "Template value for ${key} must be single-line." >&2
      return 1
    fi
    if [[ "$value" == *'{{'* || "$value" == *'}}'* ]]; then
      echo "Template value for ${key} contains template delimiters." >&2
      return 1
    fi

    content="${content//\{\{${key}\}\}/${value}}"
  done

  printf '%s\n' "$content" > "$output_path"
}

main() {
  local charts_tests charts_failures charts_errors charts_skipped
  local app_tests app_failures app_errors app_skipped
  local playground_tests playground_failures playground_errors playground_skipped
  local android_tests android_failures android_errors android_skipped

  read -r charts_tests charts_failures charts_errors charts_skipped < <(collect_suite_counts "${CHARTS_RESULT_DIRS[@]}")
  read -r app_tests app_failures app_errors app_skipped < <(collect_suite_counts "${APP_RESULT_DIRS[@]}")
  read -r playground_tests playground_failures playground_errors playground_skipped < <(collect_suite_counts "${PLAYGROUND_RESULT_DIRS[@]}")
  read -r android_tests android_failures android_errors android_skipped < <(collect_suite_counts "${ANDROID_SCREENSHOT_RESULT_DIRS[@]}")

  local behavior_tests behavior_failures behavior_errors
  read -r behavior_tests behavior_failures behavior_errors < <(collect_behavior_counts)

  local gradle_broken total_tests total_failures total_errors total_skipped total_line
  total_tests=$((charts_tests + app_tests + playground_tests + android_tests + behavior_tests))
  total_failures=$((charts_failures + app_failures + playground_failures + android_failures + behavior_failures))
  total_errors=$((charts_errors + app_errors + playground_errors + android_errors + behavior_errors))
  total_skipped=$((charts_skipped + app_skipped + playground_skipped + android_skipped))
  gradle_broken="$(gradle_step_broken)"
  total_line="$(total_line_text "$total_tests" "$total_failures" "$total_errors" "$total_skipped" "$gradle_broken")"

  local charts_line app_line playground_line android_line behavior_line
  charts_line="$(line_text "Charts" "$charts_tests" "$charts_failures" "$charts_errors" "$charts_skipped")"
  app_line="$(line_text "App" "$app_tests" "$app_failures" "$app_errors" "$app_skipped")"
  playground_line="$(line_text "Playground" "$playground_tests" "$playground_failures" "$playground_errors" "$playground_skipped")"
  android_line="$(line_text "Android screenshot" "$android_tests" "$android_failures" "$android_errors" "$android_skipped")"
  behavior_line="$(line_text "CI behavior (total)" "$behavior_tests" "$behavior_failures" "$behavior_errors" 0)"

  render_template \
    "$SUMMARY_TEMPLATE_MD" \
    "$SUMMARY_FILE" \
    charts_line "$charts_line" \
    app_line "$app_line" \
    playground_line "$playground_line" \
    android_screenshot_line "$android_line" \
    ci_behavior_total_line "$behavior_line" \
    total_line "$total_line"

  render_template \
    "$SUMMARY_TEMPLATE_JSON" \
    "$SUMMARY_JSON_FILE" \
    charts_tests "$charts_tests" \
    charts_failures "$charts_failures" \
    charts_errors "$charts_errors" \
    app_tests "$app_tests" \
    app_failures "$app_failures" \
    app_errors "$app_errors" \
    playground_tests "$playground_tests" \
    playground_failures "$playground_failures" \
    playground_errors "$playground_errors" \
    android_screenshot_tests "$android_tests" \
    android_screenshot_failures "$android_failures" \
    android_screenshot_errors "$android_errors" \
    ci_behavior_tests "$behavior_tests" \
    ci_behavior_failures "$behavior_failures" \
    ci_behavior_errors "$behavior_errors" \
    total_tests "$total_tests" \
    total_failures "$((total_failures + gradle_broken))" \
    total_errors "$total_errors"

  if [[ "$SKIP_STEP_SUMMARY" != "true" && -n "${GITHUB_STEP_SUMMARY:-}" ]]; then
    cat "$SUMMARY_FILE" >> "$GITHUB_STEP_SUMMARY"
  fi

  cat "$SUMMARY_FILE"
}

main "$@"
