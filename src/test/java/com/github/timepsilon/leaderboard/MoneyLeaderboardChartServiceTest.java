package com.github.timepsilon.leaderboard;

import com.github.timepsilon.database.entity.BalanceHistoryPoint;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyLeaderboardChartServiceTest {

    private static final Instant BASE = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void buildChartSeriesPicksTopPlayersByLatestBalance() {
        List<BalanceHistoryPoint> history = List.of(
                point("alice", 0, 100),
                point("alice", 1, 150),
                point("bob", 0, 500),
                point("bob", 1, 450),
                point("carol", 0, 300),
                point("carol", 1, 320)
        );

        List<ChartSeries> series = MoneyLeaderboardChartService.buildChartSeries(history, 2, 8);

        assertEquals(2, series.size());
        assertEquals("bob", series.get(0).username());
        assertEquals("carol", series.get(1).username());
        assertEquals(450, series.get(0).points().get(1).money());
    }

    @Test
    void buildChartSeriesDownsamplesPoints() {
        List<BalanceHistoryPoint> history = List.of(
                point("alice", 0, 10),
                point("alice", 1, 20),
                point("alice", 2, 30),
                point("alice", 3, 40),
                point("alice", 4, 50)
        );

        List<ChartSeries> series = MoneyLeaderboardChartService.buildChartSeries(history, 1, 3);

        assertEquals(1, series.size());
        assertEquals(3, series.get(0).points().size());
        assertEquals(10, series.get(0).points().get(0).money());
        assertEquals(30, series.get(0).points().get(1).money());
        assertEquals(50, series.get(0).points().get(2).money());
    }

    @Test
    void buildChartSeriesReturnsEmptyForInvalidInput() {
        assertTrue(MoneyLeaderboardChartService.buildChartSeries(List.of(), 4, 32).isEmpty());
        assertTrue(MoneyLeaderboardChartService.buildChartSeries(null, 4, 32).isEmpty());
    }

    @Test
    void latestBalanceUsesMostRecentTimestamp() {
        List<BalanceHistoryPoint> points = List.of(
                point("alice", 0, 100),
                point("alice", 5, 250),
                point("alice", 2, 200)
        );

        assertEquals(250, MoneyLeaderboardChartService.latestBalance(points));
    }

    private static BalanceHistoryPoint point(String username, int minuteOffset, int money) {
        return new BalanceHistoryPoint(username, BASE.plusSeconds(minuteOffset * 60L), money);
    }
}
