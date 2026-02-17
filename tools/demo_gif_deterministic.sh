#!/usr/bin/env bash
set -euo pipefail

SCRIPT_NAME="$(basename "$0")"
SCRIPT_PATH="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/$(basename "${BASH_SOURCE[0]}")"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DOCS_VERSION="${DOCS_VERSION:-snapshot}"
OUTPUT_DIR="${OUTPUT_DIR:-$ROOT_DIR/docs/content/$DOCS_VERSION/wiki/assets}"
TMP_ROOT="${TMP_ROOT:-${TMPDIR:-/tmp}/charts-demo-gif-deterministic}"

APP_ID="${APP_ID:-io.github.dautovicharis.charts.app}"
ADB_BIN="${ADB_BIN:-}"
GIF_WIDTH="${GIF_WIDTH:-540}"
GIF_HEIGHT="${GIF_HEIGHT:-}"
GIF_FPS="${GIF_FPS:-50}"
GIF_OPT_LEVEL="${GIF_OPT_LEVEL:-3}"
GIF_OPT_LOSSY="${GIF_OPT_LOSSY:-0}"
GIF_OPT_COLORS="${GIF_OPT_COLORS:-256}"
FRAME_STEP_MS="${FRAME_STEP_MS:-}"
INTRO_FRAMES="${INTRO_FRAMES:-}"
INTERACTION_FRAMES="${INTERACTION_FRAMES:-}"
KEEP_TMP="${KEEP_TMP:-0}"
VERBOSE="${VERBOSE:-0}"

DEMO_NAME=""
DEVICE_SERIAL=""
ADB_CMD=()
ADB=()
RUN_DIR=""
FRAMES_DIR=""
NORMALIZED_FRAMES_DIR=""
BASE_GIF=""
OUTPUT_GIF=""
REMOTE_FRAMES_SUBDIR=""
REMOTE_FRAMES_DIR=""
INTERNAL_FRAMES_DIR=""
INTERNAL_FRAMES_PACKAGE=""
RESOLVED_GIF_HEIGHT=""
TEST_CLASS="io.github.dautovicharis.charts.app.recording.DeterministicGifFrameCaptureTest#captureDemoFrames"

SUPPORTED_DEMOS=(
  pie_default
  line_default
  multi_line_default
  bar_default
  stacked_bar_default
  stacked_area_default
  radar_default
)

log() {
  printf '[demo_gif_deterministic] %s\n' "$*"
}

die() {
  printf '[demo_gif_deterministic] ERROR: %s\n' "$*" >&2
  exit 1
}

usage() {
  cat <<EOF
Usage:
  $SCRIPT_NAME <demo_name>
  $SCRIPT_NAME --all
  $SCRIPT_NAME --help

Examples:
  ./tools/demo_gif_deterministic.sh pie_default
  ./tools/demo_gif_deterministic.sh --all
  ADB_SERIAL=emulator-5554 ./tools/demo_gif_deterministic.sh radar_default
  ADB_BIN="$HOME/Library/Android/sdk/platform-tools/adb" ./tools/demo_gif_deterministic.sh multi_line_default

Requires GIF optimizer in PATH:
  gifsicle

Supported demos:
  ${SUPPORTED_DEMOS[*]}

Environment overrides:
  APP_ID                     (default: $APP_ID)
  ADB_BIN                    (default: auto-detect adb, including Android SDK platform-tools)
  ADB_SERIAL                 (default: auto-detect single connected device)
  GIF_WIDTH                  (default: $GIF_WIDTH)
  GIF_HEIGHT                 (default: auto from pulled PNG frames)
  GIF_FPS                    (default: $GIF_FPS)
  GIF_OPT_LEVEL              (default: $GIF_OPT_LEVEL)
  GIF_OPT_LOSSY              (default: $GIF_OPT_LOSSY)
  GIF_OPT_COLORS             (default: $GIF_OPT_COLORS)
  FRAME_STEP_MS              (default per demo)
  INTRO_FRAMES               (default per demo)
  INTERACTION_FRAMES         (default per demo)
  DOCS_VERSION               (default: $DOCS_VERSION; ignored when OUTPUT_DIR is set)
  OUTPUT_DIR                 (default: $OUTPUT_DIR)
  KEEP_TMP                   (1 to keep temp files)
  VERBOSE                    (1 for verbose ffmpeg output)
EOF
}

run_all_demos() {
  local total="${#SUPPORTED_DEMOS[@]}"
  local index=0
  local demo

  log "Running all supported demos sequentially ($total total)"
  for demo in "${SUPPORTED_DEMOS[@]}"; do
    index=$((index + 1))
    log "[$index/$total] Starting '$demo'"
    DOCS_VERSION="$DOCS_VERSION" OUTPUT_DIR="$OUTPUT_DIR" "$SCRIPT_PATH" "$demo"
  done
}

require_cmd() {
  local cmd="$1"
  command -v "$cmd" >/dev/null 2>&1 || die "Missing required command: $cmd"
}

require_gifsicle() {
  command -v gifsicle >/dev/null 2>&1 || die "Missing GIF optimizer: install 'gifsicle'."
}

resolve_adb_cmd() {
  if [[ -n "$ADB_BIN" ]]; then
    [[ -x "$ADB_BIN" ]] || die "ADB_BIN is not executable: $ADB_BIN"
    ADB_CMD=("$ADB_BIN")
    return
  fi

  local -a candidates=(
    "${ANDROID_SDK_ROOT:-}/platform-tools/adb"
    "${ANDROID_HOME:-}/platform-tools/adb"
    "$HOME/Library/Android/sdk/platform-tools/adb"
    "$HOME/Android/Sdk/platform-tools/adb"
  )

  local candidate
  for candidate in "${candidates[@]}"; do
    if [[ -n "$candidate" && -x "$candidate" ]]; then
      ADB_CMD=("$candidate")
      return
    fi
  done

  if command -v adb >/dev/null 2>&1; then
    ADB_CMD=("$(command -v adb)")
    return
  fi

  die "Could not find adb. Set ADB_BIN or install Android SDK platform-tools."
}

select_device() {
  if [[ -n "${ADB_SERIAL:-}" ]]; then
    printf '%s\n' "$ADB_SERIAL"
    return
  fi

  local -a devices=()
  while IFS= read -r device; do
    [[ -n "$device" ]] || continue
    devices+=("$device")
  done < <("${ADB_CMD[@]}" devices | awk 'NR > 1 && $2 == "device" { print $1 }')

  if [[ ${#devices[@]} -eq 0 ]]; then
    die "No connected Android device/emulator detected via ${ADB_CMD[*]}."
  fi

  if [[ ${#devices[@]} -gt 1 ]]; then
    die "Multiple devices detected. Set ADB_SERIAL to target one device."
  fi

  printf '%s\n' "${devices[0]}"
}

resolve_demo() {
  local demo="$1"
  DEMO_NAME="$demo"

  local supported=0
  local item
  for item in "${SUPPORTED_DEMOS[@]}"; do
    if [[ "$item" == "$demo" ]]; then
      supported=1
      break
    fi
  done
  if [[ "$supported" != "1" ]]; then
    die "Unsupported demo '$demo'. Supported: ${SUPPORTED_DEMOS[*]}"
  fi

  REMOTE_FRAMES_SUBDIR="deterministic-gif/${demo}"
  [[ -n "$FRAME_STEP_MS" ]] || FRAME_STEP_MS="20"

  case "$demo" in
    pie_default)
      [[ -n "$INTRO_FRAMES" ]] || INTRO_FRAMES="65"
      [[ -n "$INTERACTION_FRAMES" ]] || INTERACTION_FRAMES="14"
      ;;
    bar_default)
      [[ -n "$INTRO_FRAMES" ]] || INTRO_FRAMES="55"
      [[ -n "$INTERACTION_FRAMES" ]] || INTERACTION_FRAMES="14"
      ;;
    line_default|multi_line_default|stacked_area_default)
      [[ -n "$INTRO_FRAMES" ]] || INTRO_FRAMES="90"
      [[ -n "$INTERACTION_FRAMES" ]] || INTERACTION_FRAMES="12"
      ;;
    stacked_bar_default)
      [[ -n "$INTRO_FRAMES" ]] || INTRO_FRAMES="70"
      [[ -n "$INTERACTION_FRAMES" ]] || INTERACTION_FRAMES="12"
      ;;
    radar_default)
      [[ -n "$INTRO_FRAMES" ]] || INTRO_FRAMES="80"
      [[ -n "$INTERACTION_FRAMES" ]] || INTERACTION_FRAMES="12"
      ;;
  esac
}

run_capture_test() {
  local -a install_cmd=(
    ./gradlew
    :androidApp:installDebug
    :androidApp:installDebugAndroidTest
  )
  local test_runner="${APP_ID}.test/androidx.test.runner.AndroidJUnitRunner"

  log "Installing debug and androidTest APKs"
  if [[ "$VERBOSE" == "1" ]]; then
    ANDROID_SERIAL="$DEVICE_SERIAL" "${install_cmd[@]}"
  else
    ANDROID_SERIAL="$DEVICE_SERIAL" "${install_cmd[@]}" --console=plain --quiet
  fi

  log "Running deterministic frame capture test: $TEST_CLASS"
  "${ADB[@]}" shell am instrument -w \
    -e class "$TEST_CLASS" \
    -e demo_name "$DEMO_NAME" \
    -e output_subdir "$REMOTE_FRAMES_SUBDIR" \
    -e frame_step_ms "$FRAME_STEP_MS" \
    -e intro_frames "$INTRO_FRAMES" \
    -e interaction_frames "$INTERACTION_FRAMES" \
    "$test_runner"
}

pull_frames() {
  log "Pulling captured PNG frames via run-as"

  local frame_list=""
  local -a package_candidates=("$APP_ID" "${APP_ID}.test")
  local pkg
  for pkg in "${package_candidates[@]}"; do
    local -a dir_candidates=(
      "./files/${REMOTE_FRAMES_SUBDIR}"
      "./cache/${REMOTE_FRAMES_SUBDIR}"
    )
    local dir
    for dir in "${dir_candidates[@]}"; do
      if [[ "$VERBOSE" == "1" ]]; then
        log "Checking package '$pkg' path '$dir'"
      fi
      frame_list="$("${ADB[@]}" shell run-as "$pkg" sh -c "find '${dir}' -type f -name 'frame-*.png' 2>/dev/null | sort" | tr -d '\r')"
      if [[ -n "$frame_list" ]]; then
        INTERNAL_FRAMES_PACKAGE="$pkg"
        INTERNAL_FRAMES_DIR="$dir"
        break
      fi
    done
    if [[ -n "$frame_list" ]]; then
      break
    fi
  done

  if [[ -z "$frame_list" ]]; then
    for pkg in "${package_candidates[@]}"; do
      if [[ "$VERBOSE" == "1" ]]; then
        log "Fallback scan in package '$pkg' from sandbox root"
      fi
      frame_list="$("${ADB[@]}" shell run-as "$pkg" sh -c "find . -type f -name 'frame-*.png' 2>/dev/null | sort" | tr -d '\r')"
      if [[ -n "$frame_list" ]]; then
        INTERNAL_FRAMES_PACKAGE="$pkg"
        INTERNAL_FRAMES_DIR="."
        break
      fi
    done
  fi

  if [[ -z "$frame_list" ]]; then
    REMOTE_FRAMES_DIR="/sdcard/Android/data/${APP_ID}/files/${REMOTE_FRAMES_SUBDIR}"
    log "No internal frames found. Trying legacy external path: $REMOTE_FRAMES_DIR"
    "${ADB[@]}" pull "${REMOTE_FRAMES_DIR}/." "$FRAMES_DIR" >/dev/null 2>&1 || true
  else
    log "Found internal frames in package '$INTERNAL_FRAMES_PACKAGE' at '$INTERNAL_FRAMES_DIR'"
    while IFS= read -r remote_file; do
      [[ -n "$remote_file" ]] || continue
      remote_file="$(printf '%s' "$remote_file" | sed 's/^[[:space:]]*//; s/[[:space:]]*$//')"
      [[ -n "$remote_file" ]] || continue
      local file_name
      file_name="$(basename "$remote_file")"
      [[ "$file_name" == frame-*.png ]] || continue
      if ! "${ADB[@]}" exec-out run-as "$INTERNAL_FRAMES_PACKAGE" cat "$remote_file" > "$FRAMES_DIR/$file_name"; then
        rm -f "$FRAMES_DIR/$file_name"
      fi
    done <<< "$frame_list"
  fi

  shopt -s nullglob
  local -a frame_files=("$FRAMES_DIR"/frame-*.png)
  shopt -u nullglob
  if [[ ${#frame_files[@]} -eq 0 ]]; then
    die "No PNG frames were pulled from device. Verify the test ran and produced output."
  fi
}

render_gif() {
  log "Generating base GIF at $BASE_GIF"
  local ffmpeg_loglevel="error"
  if [[ "$VERBOSE" == "1" ]]; then
    ffmpeg_loglevel="info"
  fi

  local -a palette_cmd=(
    ffmpeg -hide_banner
    -loglevel "$ffmpeg_loglevel"
    -y
    -framerate "$GIF_FPS"
    -i "$NORMALIZED_FRAMES_DIR/frame-%04d.png"
    -vf "palettegen=stats_mode=full"
    -frames:v 1
    "$RUN_DIR/palette.png"
  )

  "${palette_cmd[@]}"

  local -a gif_cmd=(
    ffmpeg -hide_banner
    -loglevel "$ffmpeg_loglevel"
    -y
    -framerate "$GIF_FPS"
    -i "$NORMALIZED_FRAMES_DIR/frame-%04d.png"
    -i "$RUN_DIR/palette.png"
    -lavfi "paletteuse=dither=bayer:bayer_scale=3:diff_mode=rectangle"
    "$BASE_GIF"
  )

  "${gif_cmd[@]}"
}

normalize_frames() {
  local scale_pad_filter
  # For light theme GIFs, switch pad color from black to white.
  scale_pad_filter="scale=${GIF_WIDTH}:${RESOLVED_GIF_HEIGHT}:flags=lanczos:force_original_aspect_ratio=decrease,pad=${GIF_WIDTH}:${RESOLVED_GIF_HEIGHT}:(ow-iw)/2:(oh-ih)/2:color=black,format=rgb24"
  log "Normalizing frames to fixed canvas ${GIF_WIDTH}x${RESOLVED_GIF_HEIGHT}"

  local ffmpeg_loglevel="error"
  if [[ "$VERBOSE" == "1" ]]; then
    ffmpeg_loglevel="info"
  fi

  mkdir -p "$NORMALIZED_FRAMES_DIR"
  ffmpeg -hide_banner \
    -loglevel "$ffmpeg_loglevel" \
    -y \
    -framerate "$GIF_FPS" \
    -i "$FRAMES_DIR/frame-%04d.png" \
    -vf "$scale_pad_filter" \
    "$NORMALIZED_FRAMES_DIR/frame-%04d.png"

  shopt -s nullglob
  local -a normalized_files=("$NORMALIZED_FRAMES_DIR"/frame-*.png)
  shopt -u nullglob
  [[ ${#normalized_files[@]} -gt 0 ]] || die "Failed to generate normalized frames."
}

resolve_gif_canvas_height() {
  if [[ -n "$GIF_HEIGHT" ]]; then
    [[ "$GIF_HEIGHT" =~ ^[0-9]+$ ]] || die "GIF_HEIGHT must be a positive integer, got: $GIF_HEIGHT"
    (( GIF_HEIGHT > 0 )) || die "GIF_HEIGHT must be greater than zero, got: $GIF_HEIGHT"
    RESOLVED_GIF_HEIGHT="$GIF_HEIGHT"
    log "Using configured GIF canvas: ${GIF_WIDTH}x${RESOLVED_GIF_HEIGHT}"
    return
  fi

  shopt -s nullglob
  local -a frame_files=("$FRAMES_DIR"/frame-*.png)
  shopt -u nullglob
  [[ ${#frame_files[@]} -gt 0 ]] || die "No frames available to resolve GIF canvas height."

  local max_scaled_height=0
  local frame dims width height scaled_height
  for frame in "${frame_files[@]}"; do
    dims="$(ffprobe -v error -select_streams v:0 -show_entries stream=width,height -of csv=p=0:s=x "$frame" | tr -d '\r')"
    width="${dims%x*}"
    height="${dims#*x}"

    if [[ -z "$width" || -z "$height" || "$width" == "$dims" || ! "$width" =~ ^[0-9]+$ || ! "$height" =~ ^[0-9]+$ ]]; then
      die "Could not read frame dimensions from $frame"
    fi

    scaled_height=$(( (height * GIF_WIDTH + width - 1) / width ))
    if (( scaled_height > max_scaled_height )); then
      max_scaled_height="$scaled_height"
    fi
  done

  (( max_scaled_height > 0 )) || die "Failed to resolve GIF canvas height from frame data."
  RESOLVED_GIF_HEIGHT="$max_scaled_height"
  log "Resolved GIF canvas to ${GIF_WIDTH}x${RESOLVED_GIF_HEIGHT} from ${#frame_files[@]} frames"
}

optimize_gif() {
  log "Optimizing GIF with gifsicle (--optimize=${GIF_OPT_LEVEL} --lossy=${GIF_OPT_LOSSY} --colors ${GIF_OPT_COLORS})"
  gifsicle \
    --no-warnings \
    --optimize="${GIF_OPT_LEVEL}" \
    --lossy="${GIF_OPT_LOSSY}" \
    --colors "${GIF_OPT_COLORS}" \
    "$BASE_GIF" \
    -o "$OUTPUT_GIF"
}

validate_optimizer_settings() {
  [[ "$GIF_OPT_LEVEL" =~ ^[1-3]$ ]] || die "GIF_OPT_LEVEL must be 1, 2, or 3 (got: $GIF_OPT_LEVEL)"
  [[ "$GIF_OPT_LOSSY" =~ ^[0-9]+$ ]] || die "GIF_OPT_LOSSY must be a non-negative integer (got: $GIF_OPT_LOSSY)"
  [[ "$GIF_OPT_COLORS" =~ ^[0-9]+$ ]] || die "GIF_OPT_COLORS must be an integer in [2, 256] (got: $GIF_OPT_COLORS)"
  (( GIF_OPT_COLORS >= 2 && GIF_OPT_COLORS <= 256 )) || die "GIF_OPT_COLORS must be in [2, 256] (got: $GIF_OPT_COLORS)"
}

summarize_outputs() {
  local output_size
  output_size="$(wc -c < "$OUTPUT_GIF" | tr -d '[:space:]')"
  log "Done:"
  log "  $OUTPUT_GIF (${output_size} bytes)"
}

cleanup() {
  local exit_code="$1"

  if [[ -n "${INTERNAL_FRAMES_DIR:-}" && -n "${INTERNAL_FRAMES_PACKAGE:-}" && -n "${ADB[*]:-}" ]]; then
    "${ADB[@]}" shell run-as "$INTERNAL_FRAMES_PACKAGE" rm -rf "$INTERNAL_FRAMES_DIR" >/dev/null 2>&1 || true
  fi

  if [[ -n "${REMOTE_FRAMES_DIR:-}" && -n "${ADB[*]:-}" ]]; then
    "${ADB[@]}" shell rm -rf "$REMOTE_FRAMES_DIR" >/dev/null 2>&1 || true
  fi

  if [[ "$KEEP_TMP" == "1" ]]; then
    if [[ -n "$RUN_DIR" ]]; then
      log "Temporary files kept in: $RUN_DIR"
    fi
  else
    if [[ -n "$RUN_DIR" && -d "$RUN_DIR" ]]; then
      rm -rf "$RUN_DIR"
    fi
  fi

  exit "$exit_code"
}

main() {
  case "${1:-}" in
    --help|-h)
      usage
      exit 0
      ;;
    --all)
      run_all_demos
      exit 0
      ;;
    "")
      usage
      exit 1
      ;;
  esac

  DEMO_NAME="$1"

  resolve_demo "$DEMO_NAME"

  require_gifsicle
  require_cmd ffmpeg
  require_cmd ffprobe
  require_cmd ./gradlew

  validate_optimizer_settings
  resolve_adb_cmd
  "${ADB_CMD[@]}" start-server >/dev/null 2>&1 || true
  DEVICE_SERIAL="$(select_device)"
  ADB=("${ADB_CMD[@]}" -s "$DEVICE_SERIAL")

  "${ADB[@]}" get-state >/dev/null 2>&1 || die "Could not connect to adb device '$DEVICE_SERIAL'."

  mkdir -p "$OUTPUT_DIR"
  mkdir -p "$TMP_ROOT"

  RUN_DIR="$TMP_ROOT/${DEMO_NAME}-$(date +%Y%m%d-%H%M%S)"
  FRAMES_DIR="$RUN_DIR/frames"
  NORMALIZED_FRAMES_DIR="$RUN_DIR/frames-normalized"
  mkdir -p "$FRAMES_DIR"

  BASE_GIF="$RUN_DIR/${DEMO_NAME}.base.gif"
  OUTPUT_GIF="$OUTPUT_DIR/${DEMO_NAME}.gif"

  log "Demo: $DEMO_NAME"
  log "Device: $DEVICE_SERIAL"
  log "adb: ${ADB_CMD[*]}"
  log "Frame step: ${FRAME_STEP_MS}ms"
  log "Intro frames: $INTRO_FRAMES"
  log "Interaction frames: $INTERACTION_FRAMES"
  log "Optimizer: gifsicle (--optimize=${GIF_OPT_LEVEL} --lossy=${GIF_OPT_LOSSY} --colors ${GIF_OPT_COLORS})"
  log "Output: $OUTPUT_GIF"

  run_capture_test
  pull_frames
  resolve_gif_canvas_height
  normalize_frames
  render_gif
  optimize_gif
  summarize_outputs
}

trap 'cleanup $?' EXIT
main "$@"
