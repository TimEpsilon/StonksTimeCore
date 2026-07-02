# Configuration serveur — StonksTimeCore3

Fichier généré au premier démarrage du serveur :

```
config/stonkstimecore-server.toml
```

NeoForge recharge ce fichier au redémarrage du serveur. Les sections `timer` et `SRE` couvrent le gameplay ; la section **`database`** contrôle la base analytique **SQLite embarquée** et le cron de sauvegarde des soldes.

## Base de données embarquée (SQLite)

Les statistiques sont écrites dans un **fichier SQLite unique**, créé automatiquement dans la sauvegarde du monde :

```
<monde>/stonkstimecore/stonkstime.db
```

Aucun serveur de base de données à installer : le mod fonctionne **à l'identique en solo et sur un serveur dédié**. Sur un serveur, Grafana peut lire ce même fichier (voir [`grafana/`](../grafana/README.md)). Le fichier contient deux tables : `banks` (snapshots des soldes) et `sct_transaction` (transactions du chronoscope).

## Section `database`

| Clé | Type | Défaut | Description |
|-----|------|--------|-------------|
| `enableSqlStats` | bool | `true` | Active les écritures analytiques SQLite (snapshots banque + transactions SCT). Activé par défaut : la base étant embarquée (aucun serveur à installer), les stats fonctionnent en solo comme sur serveur. Passer à `false` pour tout désactiver (aucune écriture, aucun cron, aucune requête de stats). |
| `bankSaveIntervalSeconds` | int | `1` | Intervalle cron (secondes réelles) entre chaque snapshot des soldes bancaires vers la table `banks`. Minimum `1`. Utilise un thread dédié, indépendant des ticks serveur. |
| `grafanaSnapshotIntervalSeconds` | int | `15` | Intervalle (secondes réelles) d'export d'une **copie non-WAL** `stonkstime-export.db` que Grafana peut lire. La base live est en WAL, dont la mémoire partagée ne peut pas être `mmap` via un bind mount Docker — Grafana doit donc lire cette copie. Mettre `0` pour désactiver (ex. si Grafana lit le fichier live en natif). |

### Exemple — stats activées (défaut)

```toml
[database]
    enableSqlStats = true
    bankSaveIntervalSeconds = 60
    grafanaSnapshotIntervalSeconds = 15
```

Les données sont écrites dans `<monde>/stonkstimecore/stonkstime.db`, et une copie `stonkstime-export.db` est exportée pour Grafana. En solo, c'est suffisant. Sur un serveur, pointer Grafana vers la copie — voir [`grafana/`](../grafana/README.md).

### Exemple — désactiver les stats

```toml
[database]
    enableSqlStats = false
```

Le mod ne crée ni n'écrit aucun fichier de stats.

## Comportement selon `enableSqlStats`

| Composant | `false` | `true` (défaut) |
|-----------|---------|-----------------|
| Ouverture du fichier SQLite au démarrage | Ignorée | Établie (`banks`, `sct_transaction`) |
| `BankSaveScheduler` (cron soldes) | Non démarré | Planifié selon `bankSaveIntervalSeconds` |
| `GrafanaSnapshotScheduler` (copie Grafana) | Non démarré | Planifié selon `grafanaSnapshotIntervalSeconds` (si > 0) |
| Écritures SCT (chronoscope) | Ignorées | Upsert dans `sct_transaction` |
| Sauvegarde soldes (déconnexion / arrêt) | Ignorée | Upsert dans `banks` |
| `DatabaseRetryHandler` | Inactif | Réessaie les écritures en attente |

Les données de gameplay (temps, monnaie Create Numismatics, état « out ») ne passent pas par cette base et ne sont pas affectées par ce réglage.

## English summary

| Key | Default | Purpose |
|-----|---------|---------|
| `enableSqlStats` | `true` | Master switch for all SQLite analytics writes (embedded DB, enabled by default) |
| `bankSaveIntervalSeconds` | `1` | Wall-clock cron interval for bank balance snapshots |
| `grafanaSnapshotIntervalSeconds` | `15` | Interval for exporting the non-WAL `stonkstime-export.db` copy Grafana reads (`0` disables) |

The analytics database is an embedded SQLite file (`<world>/stonkstimecore/stonkstime.db`) — no external database server is required, and it behaves identically in singleplayer and on a dedicated server.
