#!/usr/bin/env pwsh
# Réinitialise les données de test PostgreSQL via Docker Compose
$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$GrafanaDir = Split-Path -Parent $ScriptDir

Push-Location $GrafanaDir
try {
    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        Write-Host "Docker requis pour injecter les données de test."
        Write-Host "  cd grafana && docker compose up -d postgres"
        Write-Host "  docker compose --profile seed run --rm seed"
        exit 1
    }

    docker compose up -d postgres
    docker compose --profile seed run --rm seed
    Write-Host "Données de test injectées dans PostgreSQL (base stonkstime)."
}
finally {
    Pop-Location
}
