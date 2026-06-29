#!/usr/bin/env bash
# Réinitialise les données de test PostgreSQL via Docker Compose
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GRAFANA_DIR="$(dirname "$SCRIPT_DIR")"

cd "$GRAFANA_DIR"

if ! command -v docker >/dev/null 2>&1; then
    echo "Docker requis pour injecter les données de test."
    echo "  cd grafana && docker compose up -d postgres"
    echo "  docker compose --profile seed run --rm seed"
    exit 1
fi

docker compose up -d postgres
docker compose --profile seed run --rm seed
echo "Données de test injectées dans PostgreSQL (base stonkstime)."
