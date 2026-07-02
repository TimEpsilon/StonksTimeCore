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
        return new BankEntry(player, username, TimeCore.getCurrentInstant(), balance);
    }

    public void bindTo(PreparedStatement statement) throws SQLException {
        statement.setString(1, player.toString());
        statement.setString(2, username);
        statement.setString(3, SqliteHelper.toIso(time));
        statement.setInt(4, money);
    }
}
