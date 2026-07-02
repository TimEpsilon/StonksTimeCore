package com.github.timepsilon.database.entity;

import com.github.timepsilon.database.SqliteHelper;
import com.github.timepsilon.utils.TimeCore;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public record BankEntry(UUID player, String username, Instant time, int money) {

    public static final String TABLE_NAME = "banks";

    public static BankEntry snapshot(UUID player, String username, int balance) {
        return snapshot(player, username, balance, TimeCore.getCurrentInstant());
    }

    /**
     * Snapshot at an explicit timestamp. Pass one shared instant for a whole save cycle so every
     * online player shares the same {@code time} (otherwise {@code COUNT(DISTINCT player)} per
     * timestamp, and {@code WHERE time = MAX(time)}, would only ever see a single player).
     */
    public static BankEntry snapshot(UUID player, String username, int balance, Instant time) {
        return new BankEntry(player, username, time, balance);
    }

    public void bindTo(PreparedStatement statement) throws SQLException {
        statement.setString(1, player.toString());
        statement.setString(2, username);
        statement.setString(3, SqliteHelper.toIso(time));
        statement.setInt(4, money);
    }
}
