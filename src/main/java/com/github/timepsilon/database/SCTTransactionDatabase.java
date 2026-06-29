package com.github.timepsilon.database;

import com.github.timepsilon.Core;
import com.github.timepsilon.utils.TimeUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.sql.*;
import java.util.Map;


public class SCTTransactionDatabase extends AbstractDatabase {

    private SCTTransactionDatabase() {}

    protected static final SCTTransactionDatabase DATABASE = new SCTTransactionDatabase();

    public void sendTransactions(ServerPlayer player, Map<Item, Integer> amountMap, Map<Item, Float> moneyMap) {
        for (Map.Entry<Item, Integer> entry : amountMap.entrySet()) {
            sendTransaction(player, entry.getKey(), entry.getValue(), moneyMap.getOrDefault(entry.getKey(), 0.0f));
        }
        try {
            statement.executeBatch();
            statement.clearBatch();
        } catch (SQLException e) {
            Core.LOGGER.error("Failed to Send Transactions to Database!", e);
        }
    }

    private void sendTransaction(ServerPlayer player, Item item, int amount, float money) {
        long hour = TimeUtils.getCurrentHour();
        byte[] uuid = TimeUtils.UUIDToBytes(player.getUUID());
        String itemID = item.toString();
        int storedMoney = (int)(money*1000);

        try {
            statement.setLong(1, hour);
            statement.setBytes(2, uuid);
            statement.setString(3, itemID);
            statement.setInt(4, amount);
            statement.setInt(5, storedMoney);

            statement.addBatch();
        } catch (SQLException e) {
            Core.LOGGER.error("Couldn't Update {} Database : ", getDatabaseName(), e);
        }
    }

    @Override
    protected void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS sct_transaction (
                hour INTEGER NOT NULL,
                player BLOB NOT NULL,
                item TEXT NOT NULL,
                amount INTEGER NOT NULL,
                money INTEGER NOT NULL,
                PRIMARY KEY (hour, player, item)
            )
            """);

            // Prepared statement
            statement = connection.prepareStatement("""
            INSERT INTO sct_transaction
            (hour, player, item, amount, money)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (hour, player, item)
            DO UPDATE SET
            amount = amount + excluded.amount,
            money = money + excluded.money;
            """);
        } catch (Exception e) {
            Core.LOGGER.error("Error Creating SCT Database.", e);
        }
    }

    public static SCTTransactionDatabase getDatabase() {
        return DATABASE;
    }

    @Override
    public String getDatabaseName() {
        return "SCTTransaction";
    }
}
