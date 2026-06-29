# Configuration serveur — StonksTimeCore3

Fichier généré au premier démarrage du serveur :

```
config/stonkstimecore-server.toml
```

NeoForge recharge ce fichier au redémarrage du serveur. Les sections `timer` et `SRE` couvrent le gameplay ; la section **`database`** contrôle PostgreSQL et le cron de sauvegarde des soldes.

## Section `database`

| Clé | Type | Défaut | Description |
|-----|------|--------|-------------|
| `enableSqlStats` | bool | `false` | Active les écritures analytiques PostgreSQL (snapshots banque + transactions SCT). Si `false`, aucune connexion JDBC, aucun cron, aucune requête de stats. |
| `host` | string | `localhost` | Hôte PostgreSQL (ignoré si `enableSqlStats = false`). |
| `port` | int | `5432` | Port PostgreSQL. |
| `database` | string | `stonkstime` | Nom de la base. |
| `user` | string | `stonkstime` | Utilisateur. |
| `password` | string | `stonkstime` | Mot de passe. |
| `bankSaveIntervalSeconds` | int | `1` | Intervalle cron (secondes réelles) entre chaque snapshot des soldes bancaires vers la table `banks`. Minimum `1`. Utilise un thread dédié, indépendant des ticks serveur. |

### Exemple — stats désactivées (défaut)

```toml
[database]
    enableSqlStats = false
```

Aucune dépendance PostgreSQL requise pour faire tourner le serveur.

### Exemple — stats activées avec Grafana

```toml
[database]
    enableSqlStats = true
    host = "localhost"
    port = 5432
    database = "stonkstime"
    user = "stonkstime"
    password = "stonkstime"
    bankSaveIntervalSeconds = 60
```

Aligner ces valeurs avec le stack Docker dans [`grafana/`](../grafana/README.md).

## Comportement selon `enableSqlStats`

| Composant | `false` (défaut) | `true` |
|-----------|------------------|--------|
| Connexion PostgreSQL au démarrage | Ignorée | Établie (`banks`, `sct_transaction`) |
| `BankSaveScheduler` (cron soldes) | Non démarré | Planifié selon `bankSaveIntervalSeconds` |
| Écritures SCT (chronoscope) | Ignorées | Upsert dans `sct_transaction` |
| Sauvegarde soldes (déconnexion / arrêt) | Ignorée | Upsert dans `banks` |
| `DatabaseRetryHandler` | Inactif | Réessaie les écritures en attente |

Les données de gameplay (temps, monnaie Create Numismatics, état « out ») ne passent pas par PostgreSQL et ne sont pas affectées par ce réglage.

## English summary

| Key | Default | Purpose |
|-----|---------|---------|
| `enableSqlStats` | `false` | Master switch for all PostgreSQL analytics writes |
| `bankSaveIntervalSeconds` | `1` | Wall-clock cron interval for bank balance snapshots |
| `host`, `port`, `database`, `user`, `password` | see above | JDBC connection (only used when stats are enabled) |
