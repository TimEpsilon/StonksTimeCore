package com.github.timepsilon.database.entity;

import com.github.timepsilon.utils.TimeUtils;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public record BankEntry(UUID player, String username, LocalDate time, int money) {

    public static final String TABLE_NAME = "banks";

    public static BankEntry snapshot(UUID player, String username, int balance) {
        return new BankEntry(player, username, TimeUtils.getCurrentDate(), balance);
    }

    public void bindTo(PreparedStatement statement) throws SQLException {
        statement.setObject(1, player);
        statement.setString(2, username);
        statement.setDate(3, Date.valueOf(time));
        statement.setInt(4, money);
    }
}
