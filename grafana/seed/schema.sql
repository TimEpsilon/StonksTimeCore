-- SQLite schema for the demo database — mirrors the tables the mod creates
-- in <world>/stonkstimecore/stonkstime.db.
-- Timestamps are stored as fixed-width ISO-8601 UTC text (YYYY-MM-DDTHH:MM:SS.SSSZ).

CREATE TABLE IF NOT EXISTS banks (
    player   TEXT    NOT NULL,
    username TEXT    NOT NULL,
    time     TEXT    NOT NULL,
    money    INTEGER NOT NULL,
    PRIMARY KEY (player, time)
);

CREATE TABLE IF NOT EXISTS sct_transaction (
    time     TEXT    NOT NULL,
    player   TEXT    NOT NULL,
    username TEXT    NOT NULL,
    item     TEXT    NOT NULL,
    amount   INTEGER NOT NULL,
    money    INTEGER NOT NULL,
    PRIMARY KEY (time, player, item)
);
