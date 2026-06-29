#!/usr/bin/env pwsh
# Génère les bases SQLite de test dans grafana/data/
$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$GrafanaDir = Split-Path -Parent $ScriptDir
$DataDir = Join-Path $GrafanaDir "data"
$SeedSql = Join-Path $ScriptDir "seed.sql"

New-Item -ItemType Directory -Force -Path $DataDir | Out-Null

Push-Location $DataDir
try {
    if (Get-Command docker -ErrorAction SilentlyContinue) {
        Push-Location $GrafanaDir
        try {
            docker compose --profile seed run --rm seed
        }
        finally {
            Pop-Location
        }
    }
    elseif (Get-Command sqlite3 -ErrorAction SilentlyContinue) {
        Get-Content $SeedSql | sqlite3
        Write-Host "Bases generees dans $DataDir"
        Write-Host "  - BankAccounts.db"
        Write-Host "  - SCTTransaction.db"
    }
    else {
        Write-Host "Docker ou sqlite3 requis."
        Write-Host "  Docker: cd grafana && docker compose --profile seed run --rm seed"
        Write-Host "  sqlite3: winget install SQLite.SQLite"
        exit 1
    }
}
finally {
    Pop-Location
}
