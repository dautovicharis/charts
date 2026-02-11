#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ASSETS_DIR="$ROOT_DIR/docs/content/snapshot/wiki/assets"
OUTPUT_GIF="${1:-$ASSETS_DIR/demo.gif}"
TMP_DIR="$(mktemp -d "${TMPDIR:-/tmp}/charts-demo-gif.XXXXXX")"

FPS=20
DURATION_SECONDS=8
COLUMNS=5
TILE_WIDTH=220
TILE_HEIGHT=260
NORMALIZED_WIDTH=540
NORMALIZED_HEIGHT=735
LOGO_BOX_WIDTH=171
LOGO_BOX_HEIGHT=104
LOGO_BG_HEX="#1F2430"
LOGO_CARD_MARGIN=10
LOGO_CARD_RADIUS=20
COLLAGE_BG_MAGICK="#F6F8FA"
COLLAGE_BG_FFMPEG="0xF6F8FA"

DEMOS=(
  bar_default
  bar_custom
  line_default
  line_custom
  multi_line_default
  multi_line_custom
  pie_default
  pie_custom
  radar_default
  stacked_bar_default
  stacked_bar_custom
  stacked_area_default
  stacked_area_custom
  radar_custom
)

log() {
  printf '[demo_gif] %s\n' "$*"
}

die() {
  printf '[demo_gif] ERROR: %s\n' "$*" >&2
  exit 1
}

cleanup() {
  rm -rf "$TMP_DIR"
}
trap cleanup EXIT

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || die "Missing required command: $1"
}

require_cmd ffmpeg
require_cmd magick

for demo in "${DEMOS[@]}"; do
  [[ -f "$ASSETS_DIR/$demo.gif" ]] || die "Missing input GIF: $ASSETS_DIR/$demo.gif"
done
[[ -f "$ASSETS_DIR/logo.png" ]] || die "Missing logo: $ASSETS_DIR/logo.png"

mkdir -p "$(dirname "$OUTPUT_GIF")"

logo_flat="$TMP_DIR/logo_flat.png"
logo_scaled="$TMP_DIR/logo_scaled.png"
logo_tile="$TMP_DIR/logo_tile.png"
composite_mp4="$TMP_DIR/demo.mp4"
palette_png="$TMP_DIR/palette.png"

magick "$ASSETS_DIR/logo.png" -background "$LOGO_BG_HEX" -alpha remove -alpha off "$logo_flat"
magick "$logo_flat" -resize "${LOGO_BOX_WIDTH}x${LOGO_BOX_HEIGHT}" "$logo_scaled"

logo_card_right=$((TILE_WIDTH - LOGO_CARD_MARGIN - 1))
logo_card_bottom=$((TILE_HEIGHT - LOGO_CARD_MARGIN - 1))
magick \
  -size "${TILE_WIDTH}x${TILE_HEIGHT}" "xc:${COLLAGE_BG_MAGICK}" \
  -fill "$LOGO_BG_HEX" \
  -draw "roundrectangle ${LOGO_CARD_MARGIN},${LOGO_CARD_MARGIN},${logo_card_right},${logo_card_bottom},${LOGO_CARD_RADIUS},${LOGO_CARD_RADIUS}" \
  "$logo_tile"
magick "$logo_tile" "$logo_scaled" -gravity center -composite "$logo_tile"

ffmpeg_inputs=()
layout_parts=()
stack_inputs=""
filter_complex=""
input_index=0

for demo in "${DEMOS[@]}"; do
  ffmpeg_inputs+=(-ignore_loop 1 -i "$ASSETS_DIR/$demo.gif")

  filter_chain="scale=${NORMALIZED_WIDTH}:${NORMALIZED_HEIGHT}:force_original_aspect_ratio=decrease,pad=${NORMALIZED_WIDTH}:${NORMALIZED_HEIGHT}:(ow-iw)/2:(oh-ih)/2:color=white,scale=${TILE_WIDTH}:${TILE_HEIGHT}:force_original_aspect_ratio=decrease,pad=${TILE_WIDTH}:${TILE_HEIGHT}:(ow-iw)/2:(oh-ih)/2:color=white"

  filter_complex+="[${input_index}:v]fps=${FPS},${filter_chain},setsar=1,trim=duration=${DURATION_SECONDS},setpts=PTS-STARTPTS[v${input_index}];"
  x=$(( (input_index % COLUMNS) * TILE_WIDTH ))
  y=$(( (input_index / COLUMNS) * TILE_HEIGHT ))
  layout_parts+=("${x}_${y}")
  stack_inputs+="[v${input_index}]"
  input_index=$((input_index + 1))
done

ffmpeg_inputs+=(-loop 1 -framerate "$FPS" -i "$logo_tile")
filter_complex+="[${input_index}:v]fps=${FPS},setsar=1,trim=duration=${DURATION_SECONDS},setpts=PTS-STARTPTS[v${input_index}];"
x=$(( (input_index % COLUMNS) * TILE_WIDTH ))
y=$(( (input_index / COLUMNS) * TILE_HEIGHT ))
layout_parts+=("${x}_${y}")
stack_inputs+="[v${input_index}]"
input_index=$((input_index + 1))

layout_string="$(IFS='|'; echo "${layout_parts[*]}")"
filter_complex+="${stack_inputs}xstack=inputs=${input_index}:layout=${layout_string}:fill=${COLLAGE_BG_FFMPEG}[stack]"

log "Rendering demo video"
ffmpeg -y "${ffmpeg_inputs[@]}" -filter_complex "$filter_complex" -map "[stack]" -pix_fmt yuv420p -loglevel error -stats "$composite_mp4"

log "Generating palette"
ffmpeg -y -i "$composite_mp4" -vf "fps=${FPS},palettegen=stats_mode=diff:max_colors=256" -loglevel error -stats "$palette_png"

log "Encoding GIF: $OUTPUT_GIF"
ffmpeg -y -i "$composite_mp4" -i "$palette_png" -lavfi "fps=${FPS},paletteuse=dither=bayer:bayer_scale=5" -loop 0 -loglevel error -stats "$OUTPUT_GIF"

if command -v gifsicle >/dev/null 2>&1; then
  log "Optimizing GIF"
  gifsicle --batch --optimize=3 --lossy=100 --colors 256 "$OUTPUT_GIF"
fi

size_bytes="$(stat -f '%z' "$OUTPUT_GIF" 2>/dev/null || stat -c '%s' "$OUTPUT_GIF")"
log "Done: $OUTPUT_GIF (${size_bytes} bytes)"
