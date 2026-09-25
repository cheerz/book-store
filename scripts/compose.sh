#!/usr/bin/env bash
# Wrapper compose : le plugin v2 (`docker compose`) n'est pas installé partout,
# le binaire v1 (`docker-compose`) si. Les runners GitHub ont v2.
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
