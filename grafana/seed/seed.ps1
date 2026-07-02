#!/usr/bin/env pwsh
# Construit la base SQLite de démonstration lue par Grafana (grafana/stonks-data/stonkstime.db).
$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$GrafanaDir = Split-Path -Parent $ScriptDir
$DbDir = Join-Path $GrafanaDir "stonks-data"
$DbFile = Join-Path $DbDir "stonkstime.db"

if (-not (Get-Command sqlite3 -ErrorAction SilentlyContinue)) {
    Write-Host "sqlite3 requis (ex: winget install SQLite.SQLite ou choco install sqlite)."
    exit 1
}

New-Item -ItemType Directory -Force -Path $DbDir | Out-Null
Remove-Item -Force -ErrorAction SilentlyContinue "$DbFile", "$DbFile-wal", "$DbFile-shm"
Get-Content (Join-Path $ScriptDir "schema.sql") -Raw | sqlite3 $DbFile
Get-Content (Join-Path $ScriptDir "seed.sql") -Raw | sqlite3 $DbFile

Write-Host "Base de démo construite : $DbFile"
Write-Host "Lancer Grafana : docker compose up -d (dans $GrafanaDir)"
