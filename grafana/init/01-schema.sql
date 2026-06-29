CREATE TABLE IF NOT EXISTS banks (
    player UUID NOT NULL,
    username TEXT NOT NULL,
    time DATE NOT NULL,
    money INTEGER NOT NULL,
    PRIMARY KEY (player, time)
);

CREATE TABLE IF NOT EXISTS sct_transaction (
    hour BIGINT NOT NULL,
    player UUID NOT NULL,
    username TEXT NOT NULL,
    item TEXT NOT NULL,
    amount INTEGER NOT NULL,
    money INTEGER NOT NULL,
    PRIMARY KEY (hour, player, item)
);
