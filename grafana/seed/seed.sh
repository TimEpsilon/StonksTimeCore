#!/usr/bin/env bash
# Génère les bases SQLite de test dans grafana/data/
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GRAFANA_DIR="$(dirname "$SCRIPT_DIR")"
DATA_DIR="$GRAFANA_DIR/data"
SEED_SQL="$SCRIPT_DIR/seed.sql"

mkdir -p "$DATA_DIR"
cd "$DATA_DIR"

if command -v sqlite3 >/dev/null 2>&1; then
    sqlite3 < "$SEED_SQL"
    echo "Bases générées dans $DATA_DIR"
    echo "  - BankAccounts.db"
    echo "  - SCTTransaction.db"
else
    echo "sqlite3 introuvable. Utilisez Docker:"
    echo "  docker run --rm -v \"${DATA_DIR}:/data\" -v \"${SEED_SQL}:/seed.sql:ro\" keinos/sqlite3 sh -c 'cd /data && sqlite3 < /seed.sql'"
    exit 1
fi
