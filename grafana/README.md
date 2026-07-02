# Grafana — Analytics SQLite StonksTimeCore3

Tableaux de bord Grafana pour analyser la base **SQLite** du mod StonksTimeCore3 (tables `banks` et `sct_transaction`).

Le mod écrit désormais dans un **fichier SQLite unique** au lieu d'un serveur PostgreSQL :

```
<monde>/stonkstimecore/stonkstime.db          # base live (mode WAL, écrite par le mod)
<monde>/stonkstimecore/stonkstime-grafana.db  # copie non-WAL, lue par Grafana
```

Aucun serveur de base de données à installer. Grafana lit via le plugin [`frser-sqlite-datasource`](https://grafana.com/grafana/plugins/frser-sqlite-datasource/).

> **Pourquoi une copie ?** La base live est en mode **WAL** (lecture+écriture simultanées), dont le fichier de mémoire partagée `-shm` ne peut pas être `mmap` à travers un **bind mount Docker** (Grafana échoue alors avec `SQLITE_IOERR_SHMMAP` / `database is locked`). Le mod exporte donc périodiquement une copie **non-WAL** `stonkstime-grafana.db` que Grafana peut lire sans souci. Cadence réglable via `grafanaSnapshotIntervalSeconds` (défaut 15 s ; `0` pour désactiver). Voir [`docs/configuration.md`](../docs/configuration.md).

## Prérequis

- [Docker](https://docs.docker.com/get-docker/) et Docker Compose
- Pour construire la base de démonstration : le CLI `sqlite3`

## Démarrage rapide (données de démonstration)

```powershell
cd grafana
./seed/seed.ps1        # Windows   (ou ./seed/seed.sh sous Linux/macOS)
docker compose up -d
```

Le script construit `grafana/stonks-data/stonkstime-grafana.db` (10 jours, 5 joueurs), et `docker compose up -d` démarre Grafana en pointant dessus par défaut.

Ouvrir **http://localhost:3000** — identifiants `admin` / `admin`. Le plugin SQLite est installé automatiquement (`GF_INSTALL_PLUGINS`), la source `StonksTime` et le dashboard « StonksTime — Analytics SQLite » sont provisionnés.

## Brancher Grafana sur un vrai serveur

Pointer Grafana sur le dossier `stonkstimecore/` du monde (qui contient la copie `stonkstime-grafana.db` exportée par le mod), via la variable `STONKS_DB_DIR` :

```powershell
# Windows PowerShell
$env:STONKS_DB_DIR = "C:\chemin\vers\serveur\world\stonkstimecore"
docker compose up -d
```

```bash
# Linux/macOS
STONKS_DB_DIR=/chemin/vers/serveur/world/stonkstimecore docker compose up -d
```

Le dossier est monté sur `/var/lib/grafana/stonks` ; la datasource pointe sur `stonkstime-grafana.db` (la copie non-WAL). Grafana ne fait que des `SELECT` (le plugin ouvre néanmoins le fichier en lecture-écriture, d'où un montage rw). `enableSqlStats` est activé par défaut côté mod (voir [`docs/configuration.md`](../docs/configuration.md)).

> Grafana doit tourner sur la **même machine** que le serveur (SQLite est un fichier local, sans protocole réseau). Astuce : tu peux aussi créer un fichier `grafana/.env` avec `STONKS_DB_DIR="..."` (ignoré par git) au lieu d'exporter la variable à chaque fois.
>
> Si tu préfères que Grafana lise le fichier **live** directement (Grafana natif hors Docker, où le `mmap` du WAL fonctionne), mets `grafanaSnapshotIntervalSeconds = 0` et pointe la datasource sur `stonkstime.db`.

## Structure des fichiers

```
grafana/
├── docker-compose.yml              # Grafana seul (plugin SQLite auto-installé)
├── provisioning/
│   ├── datasources/sqlite.yaml     # Source frser-sqlite-datasource
│   ├── dashboards/default.yaml     # Chargement auto des dashboards
│   └── alerting/stonks-alerts.yaml # Alerte solde anormal (> 3× moyenne)
├── dashboards/
│   └── stonks-analytics.json       # Dashboard principal
├── seed/
│   ├── schema.sql                  # Schéma SQLite (miroir des tables du mod)
│   ├── seed.sql                    # Données de démo
│   ├── seed.ps1                    # Build de la base de démo (Windows)
│   └── seed.sh                     # Build de la base de démo (Linux/macOS)
└── stonks-data/                    # (généré) base de démo, ignoré par git
```

## Service Docker

| Service   | Port | Rôle |
|-----------|------|------|
| `grafana` | 3000 | Dashboards et alertes (plugin SQLite) |

| Variable | Défaut | Rôle |
|----------|--------|------|
| `STONKS_DB_DIR` | `./stonks-data` | Dossier hôte contenant `stonkstime-grafana.db`, monté dans Grafana |

## Réinitialiser les données de démonstration

```powershell
./seed/seed.ps1   # Windows
./seed/seed.sh    # Linux/macOS
```

Les données couvrent **10 jours** (20–29 juin 2025), **5 joueurs** (Alice, Bob, Charlie, Diana, Eve) et plusieurs items Minecraft. Charlie a un solde anormalement élevé à partir du jour 7 pour tester l'alerte.

## Schéma

Les horodatages sont stockés en **texte ISO-8601 UTC largeur fixe** (`2025-06-20T08:00:00.000Z`) : l'ordre lexical correspond à l'ordre chronologique, et le plugin SQLite les interprète nativement comme du temps (`timeColumns`).

### Table `banks`

| Colonne  | Type   | Description              |
|---------|--------|--------------------------|
| player  | TEXT   | UUID joueur (clé)        |
| username| TEXT   | Nom d'affichage joueur   |
| time    | TEXT   | Horodatage ISO-8601 UTC  |
| money   | INT    | Solde bancaire           |

### Table `sct_transaction`

| Colonne  | Type   | Description                              |
|---------|--------|------------------------------------------|
| time    | TEXT   | Horodatage ISO-8601 UTC (clé)            |
| player  | TEXT   | UUID joueur (clé)                        |
| username| TEXT   | Nom d'affichage joueur                   |
| item    | TEXT   | ID item (`minecraft:diamond`, etc.) (clé)|
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

## Notes techniques

- Source de données : plugin [`frser-sqlite-datasource`](https://grafana.com/grafana/plugins/frser-sqlite-datasource/) (installé au démarrage via `GF_INSTALL_PLUGINS`)
- Requêtes : champ `queryText` + `timeColumns` (au lieu de `rawSql` PostgreSQL)
- Troncature horaire en SQLite : `substr(time, 1, 13) || ':00:00.000Z'` (remplace `date_trunc('hour', …)`)
- Les montants SCT sont convertis avec `money / 1000.0`
- Les UUID joueurs sont stockés en texte ; `username` sert à l'affichage
- Grafana lit `stonkstime-grafana.db` (copie non-WAL exportée par le mod via `VACUUM INTO`), pas la base live WAL — voir l'encadré en haut du fichier
