# StonksTimeCore

Mod cœur de **StonksLand V3** pour Minecraft **1.21.1** (NeoForge **21.1.211**). Il implémente une économie basée sur le **temps** : les joueurs convertissent leur monnaie (Create Numismatics) en temps de jeu ; lorsque le temps est épuisé, ils sont éliminés (« out »). Le mod ajoute des machines Create, des événements aléatoires, des sorts Iron's Spellbooks et une couche SQLite pour l'analyse des données économiques.

> **Note :** mod conçu pour StonksLand V3 — ne pas utiliser seul en production.

| | |
|---|---|
| **Identifiant** | `stonkstimecore` |
| **Version** | `0.0.1` |
| **Auteur** | TimEpsilon |
| **Licence** | All Rights Reserved |

## Fonctionnalités principales

- **Économie temporelle** — chaque seconde, le compte bancaire du joueur est débité et converti en temps restant ; la santé varie selon les seuils configurables (temps sûr / danger).
- **SCT (Stonks Conversion Time)** — valeur temporelle associée aux objets via des data maps ; affichée dans les infobulles.
- **Chronoscope temporel Stonks** — bloc Create (stress élevé) qui enregistre les transactions SCT des joueurs.
- **Machine à sous** — déclenche des événements Stonks aléatoires (gain/perte d'argent, téléportation, effets Pehkui, etc.).
- **Événements Stonks (SRE)** — 16 types gérés par `StonksEventManager` (ex. `WIN_MONEY`, `LUCKY_SCT`, `HOT_POTATO`, `MIRROR`…).
- **Commandes admin** — `/stc` pour gérer timers, statut « out », événements, équivalences et loots.
- **Intégrations** — Create, Create Numismatics, Iron's Spellbooks, Pehkui, GeckoLib, Randomium, Moonlight, Sophisticated Core.

## Prérequis

- **Java 21** (toolchain Gradle)
- **Gradle** — le wrapper du projet (`gradlew` / `gradlew.bat`) est inclus

## Compilation

```bash
./gradlew build
```

Le JAR du mod est produit dans `build/libs/`.

## Exécution en développement

Configurations Gradle définies dans `build.gradle` :

| Tâche | Description |
|---|---|
| `./gradlew runClient` | Client Minecraft |
| `./gradlew runClient2` | Second client (joueur `Dev2`) |
| `./gradlew runServer` | Serveur dédié (`--nogui`) |
| `./gradlew runData` | Génération de données |
| `./gradlew runGameTestServer` | Serveur de tests GameTest |

Des configurations VS Code sont également disponibles dans `.vscode/launch.json`.

Le répertoire de jeu par défaut est `run/`.

## Base de données et analytique

Le mod persiste des données SQLite dans le dossier du monde :

```
<monde>/stonkstimecore/
├── BankAccounts.db
└── SCTTransaction.db
```

| Fichier | Rôle |
|---|---|
| `BankAccounts.db` | Snapshots quotidiens des soldes bancaires (Create Numismatics), table `banks` (`player`, `time`, `money`). |
| `SCTTransaction.db` | Transactions SCT agrégées par heure, table `sct_transaction` (`hour`, `player`, `item`, `amount`, `money`). |

Architecture :

- `MoneyDatabase` / `BankDao` / `BankEntry` — soldes bancaires
- `SCTTransactionDatabase` / `SctTransactionDao` / `SctTransactionEntry` — transactions du chronoscope
- `SqliteHelper` — connexions SQLite (mode WAL, `synchronous=NORMAL`)

Les bases sont ouvertes au démarrage du serveur et fermées à l'arrêt (`NeoForgeEventsManager`). Les soldes sont aussi sauvegardés périodiquement via `TimerHandler`.

### Grafana (dashboards)

Un stack Docker Grafana est fourni dans [`grafana/`](grafana/) pour visualiser les bases SQLite (soldes, transactions SCT, alertes). Voir [`grafana/README.md`](grafana/README.md) pour le détail.

```bash
cd grafana
docker compose --profile seed run --rm seed   # données de test
docker compose up -d                          # http://localhost:3000 (admin/admin)
```

Pour les données réelles, copier `BankAccounts.db` et `SCTTransaction.db` depuis `<monde>/stonkstimecore/` vers `grafana/data/`.

## Tests

```bash
./gradlew test
```

Le projet inclut un test de charge JUnit 5 :

- `SctTransactionDaoStressTest` — vérifie que 200 upserts SQLite s'exécutent en moins de 5 secondes.

## Structure du projet

```
src/main/java/com/github/timepsilon/
├── Core.java                 # Point d'entrée du mod
├── block/                    # Blocs (chronoscope, machine à sous)
├── commands/                 # Commandes /stc
├── database/                 # Couche SQLite (DAO, entités)
├── stonksevent/              # Événements Stonks aléatoires
├── time/                     # Timer, statut « out » des joueurs
├── ironsspellbooks/          # Sorts personnalisés
├── config/                   # Configuration serveur/client
└── utils/                    # TimeUtils, FileManager, Scheduler

src/main/resources/           # Assets, data packs, mixins
src/test/java/                # Tests unitaires
libs/                         # Dépendances locales (JAR)
grafana/                      # Stack Docker Grafana + dashboards + seed
```

## Dépendances requises (runtime)

Create, Randomium, Moonlight, Create Numismatics, GeckoLib, Pehkui et Sophisticated Core sont déclarés comme dépendances obligatoires dans `neoforge.mods.toml`. Des mods additionnels (Iron's Spellbooks, Curios, etc.) sont embarqués via `libs/` ou Maven.

## Licence

**All Rights Reserved** — voir `mod_license` dans `gradle.properties`.
