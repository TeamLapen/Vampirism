#!/usr/bin/env bash
set -euo pipefail

find projects/vampirism/src/main/resources/assets/vampirism/sounds \
     projects/faction/src/main/resources/assets/factionapi/sounds \
     -name "*.ogg" | while IFS= read -r f; do
  raw=$(ffprobe -v error -show_entries stream=channels:stream=codec_name:stream=sample_rate -of default=noprint_wrappers=1 "$f" 2>&1)
  info=$(echo "$raw" | grep -v "CRC mismatch")
  has_crc=$(echo "$raw" | grep -c "CRC mismatch" || true)
  if [ "$info" = "codec_name=vorbis
sample_rate=44100
channels=1" ] && [ "$has_crc" -eq 0 ]; then
    echo "Already mono vorbis, skipping: $f"
    continue
  fi
  [ "$has_crc" -gt 0 ] && echo "CRC mismatch, reconverting: $f"
  tmp="${f%.ogg}_tmp.ogg"
  if ffmpeg -nostdin -i "$f"  -ar 44100 -ac 1 -c:a libvorbis -y "$tmp"; then
    mv "$tmp" "$f"
    echo "Converted: $f"
  else
    echo "ffmpeg failed for: $f" >&2
    rm -f "$tmp"
  fi
done
