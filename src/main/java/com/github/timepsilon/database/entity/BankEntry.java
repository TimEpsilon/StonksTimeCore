package com.github.timepsilon.database.entity;

import com.github.timepsilon.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public record BankEntry(UUID player, String username, Instant time, int money) {

    public static final String TABLE_NAME = "banks";

    public static BankEntry snapshot(UUID player, String username, int balance) {
        return new BankEntry(player, username, TimeUtils.getCurrentInstant(), balance);
    }

    public void bindTo(PreparedStatement statement) throws SQLException {
        statement.setObject(1, player);
        statement.setString(2, username);
        statement.setObject(3, time);
        statement.setInt(4, money);
    }
}
