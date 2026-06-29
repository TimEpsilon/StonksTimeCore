package com.github.timepsilon.leaderboard;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyLeaderboardServiceTest {

    @Test
    void computeTopNSortsByBalanceDescending() {
        List<MoneyLeaderboardService.AccountSnapshot> accounts = List.of(
                new MoneyLeaderboardService.AccountSnapshot("alice", 100),
                new MoneyLeaderboardService.AccountSnapshot("bob", 500),
                new MoneyLeaderboardService.AccountSnapshot("carol", 250)
        );

        List<LeaderboardEntry> top = MoneyLeaderboardService.computeTopN(accounts, 3);

        assertEquals(3, top.size());
        assertEquals("bob", top.get(0).username());
        assertEquals(500, top.get(0).balance());
        assertEquals(1, top.get(0).rank());
        assertEquals("carol", top.get(1).username());
        assertEquals("alice", top.get(2).username());
    }

    @Test
    void computeTopNRespectsLimit() {
        List<MoneyLeaderboardService.AccountSnapshot> accounts = List.of(
                new MoneyLeaderboardService.AccountSnapshot("a", 10),
                new MoneyLeaderboardService.AccountSnapshot("b", 20),
                new MoneyLeaderboardService.AccountSnapshot("c", 30)
        );

        List<LeaderboardEntry> top = MoneyLeaderboardService.computeTopN(accounts, 2);

        assertEquals(2, top.size());
        assertEquals("c", top.get(0).username());
        assertEquals("b", top.get(1).username());
    }

    @Test
    void computeTopNReturnsEmptyForInvalidInput() {
        assertTrue(MoneyLeaderboardService.computeTopN(List.of(), 8).isEmpty());
        assertTrue(MoneyLeaderboardService.computeTopN(null, 8).isEmpty());
        assertTrue(MoneyLeaderboardService.computeTopN(List.of(new MoneyLeaderboardService.AccountSnapshot("a", 1)), 0).isEmpty());
    }

    @Test
    void leaderboardEntryRoundTripSerialization() {
        LeaderboardEntry entry = new LeaderboardEntry(3, "player_one", 12345);
        LeaderboardEntry restored = LeaderboardEntry.deserialize(entry.serialize());
        assertEquals(entry, restored);
    }
}
