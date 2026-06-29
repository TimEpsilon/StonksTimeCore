package com.github.timepsilon.database.pending;

import com.github.timepsilon.database.entity.BankEntry;
import com.github.timepsilon.database.entity.SctTransactionEntry;

import java.util.ArrayList;
import java.util.List;

public final class PendingWritesSnapshot {

    public static final String FILE_NAME = "pending_writes.json";

    public List<BankEntryDto> banks = new ArrayList<>();
    public List<SctTransactionEntryDto> sctTransaction = new ArrayList<>();

    public record BankEntryDto(String player, String username, String time, int money) {
        static BankEntryDto from(BankEntry entry) {
            return new BankEntryDto(
                    entry.player().toString(),
                    entry.username(),
                    entry.time().toString(),
                    entry.money()
            );
        }

        BankEntry toEntry() {
            return new BankEntry(
                    java.util.UUID.fromString(player),
                    username,
                    java.time.LocalDate.parse(time),
                    money
            );
        }
    }

    public record SctTransactionEntryDto(
            long hour,
            String player,
            String username,
            String itemId,
            int amount,
            int storedMoney
    ) {
        static SctTransactionEntryDto from(SctTransactionEntry entry) {
            return new SctTransactionEntryDto(
                    entry.hour(),
                    entry.player().toString(),
                    entry.username(),
                    entry.itemId(),
                    entry.amount(),
                    entry.storedMoney()
            );
        }

        SctTransactionEntry toEntry() {
            return new SctTransactionEntry(
                    hour,
                    java.util.UUID.fromString(player),
                    username,
                    itemId,
                    amount,
                    storedMoney
            );
        }
    }
}
