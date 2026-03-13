#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 0 ]]; then
  echo "Usage: $0" >&2
  exit 1
fi

resolved_version="$(
  ./gradlew -q currentVersion \
    | awk 'NF {v=$NF} END {gsub(/\r/, "", v); print v}'
)"

if [[ -z "${resolved_version:-}" ]]; then
  echo "Failed to resolve version from Axion currentVersion task." >&2
  exit 1
fi

if [[ "${resolved_version}" != *"-SNAPSHOT" ]]; then
  echo "Expected a -SNAPSHOT current version (current=${resolved_version})." >&2
  exit 1
fi

release_version="${resolved_version%-SNAPSHOT}"
if [[ ! "$release_version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Resolved version is not SemVer-compatible: ${release_version}" >&2
  exit 1
fi

echo "$release_version"
