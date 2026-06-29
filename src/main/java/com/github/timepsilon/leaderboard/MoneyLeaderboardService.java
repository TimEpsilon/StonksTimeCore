package com.github.timepsilon.leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class MoneyLeaderboardService {

    public static final int DEFAULT_DISPLAY_COUNT = 8;

    private MoneyLeaderboardService() {}

    public record AccountSnapshot(String username, int balance) {}

    public static List<LeaderboardEntry> computeTopN(List<AccountSnapshot> accounts, int limit) {
        if (accounts == null || accounts.isEmpty() || limit <= 0) {
            return List.of();
        }

        List<AccountSnapshot> sorted = accounts.stream()
                .sorted(Comparator.comparingInt(AccountSnapshot::balance).reversed())
                .limit(limit)
                .toList();

        List<LeaderboardEntry> result = new ArrayList<>(sorted.size());
        for (int i = 0; i < sorted.size(); i++) {
            AccountSnapshot snapshot = sorted.get(i);
            result.add(new LeaderboardEntry(i + 1, snapshot.username(), snapshot.balance()));
        }
        return result;
    }
}
