package com.github.timepsilon.database.entity;

import com.github.timepsilon.database.SqliteHelper;
import com.github.timepsilon.utils.TimeCore;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public record SctTransactionEntry(Instant time, UUID player, String username, String itemId, int amount, int storedMoney) {

    public static final String TABLE_NAME = "sct_transaction";

    public static SctTransactionEntry from(ServerPlayer player, Item item, int amount, float money) {
        return new SctTransactionEntry(
                TimeCore.getCurrentHourStart(),
                player.getUUID(),
                player.getGameProfile().getName(),
                item.toString(),
                amount,
                (int) (money * 1000)
        );
    }

    public void bindTo(PreparedStatement statement) throws SQLException {
        statement.setString(1, SqliteHelper.toIso(time));
        statement.setString(2, player.toString());
        statement.setString(3, username);
        statement.setString(4, itemId);
        statement.setInt(5, amount);
        statement.setInt(6, storedMoney);
    }

    public float moneyAsFloat() {
        return storedMoney / 1000.0f;
    }
}
