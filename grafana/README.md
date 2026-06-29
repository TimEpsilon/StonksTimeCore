# Grafana — Analytics SQLite StonksTimeCore3

Tableaux de bord Grafana pour analyser les bases SQLite du mod StonksTimeCore3 (`BankAccounts.db` et `SCTTransaction.db`).

## Prérequis

- [Docker](https://docs.docker.com/get-docker/) et Docker Compose
- (Optionnel) `sqlite3` en ligne de commande pour injecter les données de test

## Démarrage rapide

```powershell
cd grafana

# 1. Générer les données de test (via Docker)
docker compose --profile seed run --rm seed

# 2. Lancer Grafana
docker compose up -d
```

Ouvrir **http://localhost:3000** — identifiants par défaut : `admin` / `admin`.

Le dossier **StonksTime** contient le dashboard « StonksTime — Analytics SQLite ».

## Structure des fichiers

```
grafana/
├── docker-compose.yml              # Grafana + plugin SQLite
├── provisioning/
│   ├── datasources/sqlite.yaml     # Sources BankAccounts + SCTTransactions
│   ├── dashboards/default.yaml     # Chargement auto des dashboards
│   └── alerting/stonks-alerts.yaml # Alerte solde anormal (> 3× moyenne)
├── dashboards/
│   └── stonks-analytics.json       # Dashboard principal
├── data/                           # Bases SQLite montées en lecture seule
│   ├── BankAccounts.db
│   └── SCTTransaction.db
└── seed/
    ├── seed.sql                    # Données de test (10 jours, 5 joueurs)
    ├── seed.ps1                    # Script Windows
    └── seed.sh                     # Script Linux/macOS
```

## Utiliser les vraies bases du serveur

Copiez les fichiers depuis le dossier serveur Minecraft vers `grafana/data/` :

| Fichier serveur       | Destination              |
|-----------------------|--------------------------|
| `BankAccounts.db`     | `grafana/data/BankAccounts.db` |
| `SCTTransaction.db`   | `grafana/data/SCTTransaction.db` |

Puis redémarrez Grafana :

```powershell
docker compose restart
```

> Les bases sont montées en **lecture seule** (`:ro`) pour éviter toute modification accidentelle.

## Injecter les données de test

### Avec sqlite3 (recommandé)

```powershell
cd grafana
.\seed\seed.ps1
```

### Avec Docker (sans sqlite3 local)

```powershell
cd grafana
docker run --rm `
  -v "${PWD}/data:/data" `
  -v "${PWD}/seed/seed.sql:/seed.sql:ro" `
  keinos/sqlite3 sh -c "cd /data && sqlite3 < /seed.sql"
```

Les données couvrent **10 jours** (20–29 juin 2025), **5 joueurs** et plusieurs items Minecraft. Charlie (UUID `…440003`) a un solde anormalement élevé à partir du jour 7 pour tester l'alerte.

## Schéma des bases

### `BankAccounts.db` — table `banks`

| Colonne | Type   | Description                    |
|---------|--------|--------------------------------|
| player  | BLOB   | UUID joueur (16 octets)        |
| time    | DATE   | Jour serveur                   |
| money   | INT    | Solde bancaire                 |

### `SCTTransaction.db` — table `sct_transaction`

| Colonne | Type   | Description                              |
|---------|--------|------------------------------------------|
| hour    | INT    | Bucket horaire (`epoch_ms / 3_600_000`)  |
| player  | BLOB   | UUID joueur                              |
| item    | TEXT   | ID item (`minecraft:diamond`, etc.)      |
| amount  | INT    | Quantité vendue                          |
| money   | INT    | Valeur × 1000 (diviser par 1000 en SQL)  |

## Dashboards inclus

| Panneau | Description |
|---------|-------------|
| Items vendus cumulés | Courbe cumulée des quantités vendues |
| Soldes joueurs par jour | Évolution du solde de chaque joueur |
| Solde total serveur | Somme de tous les soldes dans le temps |
| Transactions par heure | Volume horaire |
| Histogrammes | Quantités, valeurs de transaction, distribution des soldes |
| Top items | Classements par quantité et par revenu |
| Valeur moyenne / joueurs actifs | Métriques complémentaires |

## Alertes

Règle provisionnée **« Joueur avec solde anormal »** : déclenchée si le solde max dépasse **3× la moyenne** des soldes du dernier jour. Configurable dans *Alerting → Alert rules*.

## Arrêt

```powershell
docker compose down
```

Pour supprimer aussi le volume Grafana interne (non utilisé ici, les dashboards sont provisionnés) :

```powershell
docker compose down -v
```

## Notes techniques

- Plugin : [frser-sqlite-datasource](https://grafana.com/grafana/plugins/frser-sqlite-datasource/) (installé via `GF_PLUGINS_PREINSTALL`)
- Les UUID joueurs sont affichés via `hex(player)` formaté en UUID standard
- Les montants SCT sont convertis avec `money / 1000.0`
- Les dates `banks.time` sont converties en timestamp avec `time || 'T00:00:00Z'`
- Les buckets horaires SCT : `datetime(hour * 3600, 'unixepoch')`
