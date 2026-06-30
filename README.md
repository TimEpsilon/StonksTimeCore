# StonksTimeCore

Core Mod for **StonksLand V3**, a Minecraft **1.21.1** Neoforge modded SMP. 

You know what they say, **Time is Money**. This has now never been more true. 
*StonksTimeCore* implements a time based economy, where every player starts with a small amount of time that they can increase by converting items to money using the **Stonks Temporal Chronoscope**. Once a player time's runs out, they go bankrupt, preventing any further trading.

> **Note :** mod made for StonksLand V3 - not to be used as a standalone.

| | |
|---|---|
| **Identifiant** | `stonkstimecore` |
| **Version** | `0.0.1` |
| **Auteur** | TimEpsilon |
| **Licence** | All Rights Reserved |
| **Special Thanks** | Aryetys, Zelytra | 

## Main Functionalities

- **Time Economy** : Every second, each player's bank account gets deducted. A high balance leads to positive effects, such as a health boost, whereas a low balance reduces your max health. Once a player dies while at 0, they go bankrupt.
- **SCT (Stonks Currency Translator)** : Each item holds a price which allows a player to sell it for money. Values are mathematically propagated so that every item is equivalent to the sum of its constituants.
- **Stonks Temporal Chronoscope** : A Create block (requiring a high amount of Stress Units) which allows to convert a set of items into their equivalent price.
- **Slot Machine** : Each interaction with a Stonks Temporal Chronoscope has a chance to yield a Golden Ticket. You can then these away on a Slot Machine to try winning a prize, or losing more than just money...
- **Stonks Random Events** : 16 fun events that are randomly sampled by the Slot Machine (or using the admin commands).
- **Admin Commands** : `/stc`. Used to modify a player's time or their bankrupt status, or to launch a Stonks Random Event. Also used for generating item and recipe files, useful if you want to make your own SCT map.
- **Dependencies** : Create, Create Numismatics, Iron's Spellbooks, Pehkui, GeckoLib, Randomium, Moonlight, Sophisticated Core.

## Prerequisites

- **Java 21** (toolchain Gradle)
- **Gradle** — le wrapper du projet (`gradlew` / `gradlew.bat`) est inclus

## Database

In order to keep a record of every transaction happening on a server, the mod allows for two **PostgreSQL** databases :

| Table | Notes                                                                                                                                |
|---|--------------------------------------------------------------------------------------------------------------------------------------|
| `banks` | Snapshots of bank accounts : `player` (UUID), `username` (TEXT), `time` (DATE), `money` (INT).                                       |
| `sct_transaction` | Record of every SCT Transaction : `hour` (BIGINT), `player` (UUID), `username` (TEXT), `item` (TEXT), `amount` (INT), `money` (INT). |

**SQL logging is disabled by default** (`enableSqlStats = false`). Should be enable only if you have a Grafana or a similar data visualizer setup.

```toml
[database]
    enableSqlStats = false          # set to true to activate
    host = "localhost"
    port = 5432
    database = "stonkstime"
    user = "stonkstime"
    password = "stonkstime"
    bankSaveIntervalSeconds = 60    # cron snapshots soldes (real time)
```

When `enableSqlStats = true`, tables are made at startup and data is flushed at shutdown (or periodically for Bank records).

### Grafana (dashboards)

A docker stack (PostgreSQL + Grafana) is setup in ['grafana/'](grafana/) in order to visualize bank balances, SCT transactions and alerts. See [`grafana/README.md`](grafana/README.md) for details.

```bash
cd grafana
docker compose up -d                          # PostgreSQL + Grafana
# http://localhost:3000 (admin/admin)
```

To reset demo data :

```bash
docker compose --profile seed run --rm seed
```

The mod and grafana share the same PostgreSQL port on `localhost:5432`.

## Licence

**All Rights Reserved** — see `mod_license` in `gradle.properties`.
