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
| `enableSqlStats` | bool | `false` | Active les écritures analytiques SQLite (snapshots banque + transactions SCT). Si `false`, aucune écriture, aucun cron, aucune requête de stats. |
| `bankSaveIntervalSeconds` | int | `1` | Intervalle cron (secondes réelles) entre chaque snapshot des soldes bancaires vers la table `banks`. Minimum `1`. Utilise un thread dédié, indépendant des ticks serveur. |

### Exemple — stats désactivées (défaut)

```toml
[database]
    enableSqlStats = false
```

Le mod ne crée ni n'écrit aucun fichier de stats.

### Exemple — stats activées

```toml
[database]
    enableSqlStats = true
    bankSaveIntervalSeconds = 60
```

Les données sont écrites dans `<monde>/stonkstimecore/stonkstime.db`. En solo, c'est suffisant. Sur un serveur, pointer Grafana vers ce fichier — voir [`grafana/`](../grafana/README.md).

## Comportement selon `enableSqlStats`

| Composant | `false` (défaut) | `true` |
|-----------|------------------|--------|
| Ouverture du fichier SQLite au démarrage | Ignorée | Établie (`banks`, `sct_transaction`) |
| `BankSaveScheduler` (cron soldes) | Non démarré | Planifié selon `bankSaveIntervalSeconds` |
| Écritures SCT (chronoscope) | Ignorées | Upsert dans `sct_transaction` |
| Sauvegarde soldes (déconnexion / arrêt) | Ignorée | Upsert dans `banks` |
| `DatabaseRetryHandler` | Inactif | Réessaie les écritures en attente |

Les données de gameplay (temps, monnaie Create Numismatics, état « out ») ne passent pas par cette base et ne sont pas affectées par ce réglage.

## English summary

| Key | Default | Purpose |
|-----|---------|---------|
| `enableSqlStats` | `false` | Master switch for all SQLite analytics writes |
| `bankSaveIntervalSeconds` | `1` | Wall-clock cron interval for bank balance snapshots |

The analytics database is an embedded SQLite file (`<world>/stonkstimecore/stonkstime.db`) — no external database server is required, and it behaves identically in singleplayer and on a dedicated server.
