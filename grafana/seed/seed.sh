#!/usr/bin/env bash
# Construit la base SQLite de démonstration lue par Grafana (grafana/stonks-data/stonkstime.db).
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GRAFANA_DIR="$(dirname "$SCRIPT_DIR")"
DB_DIR="$GRAFANA_DIR/stonks-data"
DB_FILE="$DB_DIR/stonkstime.db"

if ! command -v sqlite3 >/dev/null 2>&1; then
    echo "sqlite3 requis (ex: apt install sqlite3 / brew install sqlite)."
    exit 1
fi

mkdir -p "$DB_DIR"
rm -f "$DB_FILE" "$DB_FILE-wal" "$DB_FILE-shm"
sqlite3 "$DB_FILE" < "$SCRIPT_DIR/schema.sql"
sqlite3 "$DB_FILE" < "$SCRIPT_DIR/seed.sql"

echo "Base de démo construite : $DB_FILE"
echo "Lancer Grafana : (cd \"$GRAFANA_DIR\" && docker compose up -d)"
