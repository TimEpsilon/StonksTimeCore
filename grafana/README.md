# Grafana — Analytics PostgreSQL StonksTimeCore3

Tableaux de bord Grafana pour analyser la base PostgreSQL du mod StonksTimeCore3 (`stonkstime` : tables `banks` et `sct_transaction`).

## Prérequis

- [Docker](https://docs.docker.com/get-docker/) et Docker Compose

## Démarrage rapide

```powershell
cd grafana
docker compose up -d
```

Ouvrir **http://localhost:3000** — identifiants par défaut : `admin` / `admin`.

Au premier démarrage, PostgreSQL crée automatiquement le schéma et charge les **données de démonstration** (10 jours, 5 joueurs). Grafana se connecte à la source `StonksTime` provisionnée automatiquement.

Le dossier **StonksTime** contient le dashboard « StonksTime — Analytics PostgreSQL ».

## Structure des fichiers

```
grafana/
├── docker-compose.yml              # PostgreSQL + Grafana
├── init/
│   ├── 01-schema.sql               # Schéma (exécuté au premier démarrage PG)
│   ├── 02-migration-username.sql   # Migration colonne username (bases existantes)
│   └── 03-seed.sql                 # Données de démo initiales
├── provisioning/
│   ├── datasources/postgres.yaml   # Source PostgreSQL native
│   ├── dashboards/default.yaml     # Chargement auto des dashboards
│   └── alerting/stonks-alerts.yaml # Alerte solde anormal (> 3× moyenne)
├── dashboards/
│   └── stonks-analytics.json       # Dashboard principal
└── seed/
    ├── seed.sql                    # Réinitialisation des données de test
    ├── seed.ps1                    # Script Windows
    └── seed.sh                     # Script Linux/macOS
```

## Services Docker

| Service   | Port | Rôle |
|-----------|------|------|
| `postgres` | 5432 | Base `stonkstime` (tables `banks`, `sct_transaction`) |
| `grafana`  | 3000 | Dashboards et alertes |

Variables d'environnement optionnelles (`.env` ou shell) :

| Variable | Défaut |
|----------|--------|
| `POSTGRES_DB` | `stonkstime` |
| `POSTGRES_USER` | `stonkstime` |
| `POSTGRES_PASSWORD` | `stonkstime` |
| `POSTGRES_PORT` | `5432` |

## Connexion du mod Minecraft

Configurer la section `database` dans le fichier serveur NeoForge (`config/stonkstimecore-server.toml`) :

```toml
[database]
    host = "localhost"
    port = 5432
    database = "stonkstime"
    user = "stonkstime"
    password = "stonkstime"
```

Le mod écrit directement dans PostgreSQL au démarrage/arrêt du serveur — plus de fichiers `.db` locaux.

## Réinitialiser les données de test

```powershell
cd grafana
docker compose --profile seed run --rm seed
```

Ou via les scripts :

```powershell
.\seed\seed.ps1   # Windows
./seed/seed.sh    # Linux/macOS
```

Les données couvrent **10 jours** (20–29 juin 2025), **5 joueurs** (Alice, Bob, Charlie, Diana, Eve) et plusieurs items Minecraft. Charlie a un solde anormalement élevé à partir du jour 7 pour tester l'alerte.

## Schéma

### Table `banks`

| Colonne  | Type   | Description              |
|---------|--------|--------------------------|
| player  | UUID   | UUID joueur (clé)        |
| username| TEXT   | Nom d'affichage joueur   |
| time    | DATE   | Jour serveur             |
| money   | INT    | Solde bancaire           |

### Table `sct_transaction`

| Colonne  | Type   | Description                              |
|---------|--------|------------------------------------------|
| hour    | BIGINT | Bucket horaire (`epoch_ms / 3_600_000`)  |
| player  | UUID   | UUID joueur (clé)                        |
| username| TEXT   | Nom d'affichage joueur                   |
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

Pour supprimer aussi les données PostgreSQL :

```powershell
docker compose down -v
```

## Notes techniques

- Source de données : plugin PostgreSQL natif de Grafana (pas de plugin tiers)
- Les noms joueurs sont affichés via la colonne `username` (l'UUID reste la clé primaire)
- Les montants SCT sont convertis avec `money / 1000.0`
- Les dates `banks.time` : `(time::timestamp AT TIME ZONE 'UTC')`
- Les buckets horaires SCT : `to_timestamp(hour * 3600) AT TIME ZONE 'UTC'`
