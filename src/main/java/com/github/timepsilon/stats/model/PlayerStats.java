package com.example.stats;

import java.util.UUID;

public class PlayerStats {

    public final UUID uuid;

    public long playTime;
    public long balance;
    public int kills;
    public int deaths;
    public int mobKills;

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
    }
}