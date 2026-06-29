package com.github.timepsilon.database.pending;

import com.github.timepsilon.database.entity.BankEntry;
import com.github.timepsilon.database.entity.SctTransactionEntry;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
                    parseInstant(time),
                    money
            );
        }
    }

    public record SctTransactionEntryDto(
            String time,
            String player,
            String username,
            String itemId,
            int amount,
            int storedMoney,
            Long hour
    ) {
        static SctTransactionEntryDto from(SctTransactionEntry entry) {
            return new SctTransactionEntryDto(
                    entry.time().toString(),
                    entry.player().toString(),
                    entry.username(),
                    entry.itemId(),
                    entry.amount(),
                    entry.storedMoney(),
                    null
            );
        }

        SctTransactionEntry toEntry() {
            return new SctTransactionEntry(
                    resolveTime(),
                    java.util.UUID.fromString(player),
                    username,
                    itemId,
                    amount,
                    storedMoney
            );
        }

        private Instant resolveTime() {
            if (time != null && !time.isBlank()) {
                return parseInstant(time);
            }
            if (hour != null) {
                return Instant.ofEpochSecond(hour * 3600L);
            }
            throw new IllegalStateException("SCT pending write missing time");
        }
    }

    private static Instant parseInstant(String value) {
        if (value.contains("T")) {
            return Instant.parse(value);
        }
        return LocalDate.parse(value).atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}
