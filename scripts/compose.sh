#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

if docker compose version >/dev/null 2>&1; then
  exec docker compose "$@"
elif command -v docker-compose >/dev/null 2>&1; then
  exec docker-compose "$@"
else
  echo "Ni 'docker compose' ni 'docker-compose' trouvés. Docker est-il démarré ? (colima start)" >&2
  exit 1
fi
