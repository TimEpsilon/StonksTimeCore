package com.github.timepsilon.database.entity;

import com.github.timepsilon.utils.TimeUtils;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public record BankEntry(UUID player, LocalDate time, int money) {

    public static final String TABLE_NAME = "banks";

    public static BankEntry snapshot(UUID player, int balance) {
        return new BankEntry(player, TimeUtils.getCurrentDate(), balance);
    }

    public void bindTo(PreparedStatement statement) throws SQLException {
        statement.setObject(1, player);
        statement.setDate(2, Date.valueOf(time));
        statement.setInt(3, money);
    }
}
