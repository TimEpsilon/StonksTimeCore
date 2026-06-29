package com.github.timepsilon.database.entity;

import com.github.timepsilon.utils.TimeUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public record SctTransactionEntry(long hour, UUID player, String itemId, int amount, int storedMoney) {

    public static final String TABLE_NAME = "sct_transaction";

    public static SctTransactionEntry from(ServerPlayer player, Item item, int amount, float money) {
        return new SctTransactionEntry(
                TimeUtils.getCurrentHour(),
                player.getUUID(),
                item.toString(),
                amount,
                (int) (money * 1000)
        );
    }

    public void bindTo(PreparedStatement statement) throws SQLException {
        statement.setLong(1, hour);
        statement.setObject(2, player);
        statement.setString(3, itemId);
        statement.setInt(4, amount);
        statement.setInt(5, storedMoney);
    }

    public float moneyAsFloat() {
        return storedMoney / 1000.0f;
    }
}
