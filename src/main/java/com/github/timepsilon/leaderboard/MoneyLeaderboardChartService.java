package com.github.timepsilon.leaderboard;

import com.github.timepsilon.database.entity.BalanceHistoryPoint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MoneyLeaderboardChartService {

    public static final int MAX_PLAYERS = 4;
    public static final int MAX_POINTS_PER_SERIES = 32;

    private static final int[] SERIES_COLORS = {
            0xFFFFD54F,
            0xFFFF7043,
            0xFF26A69A,
            0xFF42A5F5
    };

    private MoneyLeaderboardChartService() {}

    public static List<ChartSeries> buildChartSeries(List<BalanceHistoryPoint> history) {
        return buildChartSeries(history, MAX_PLAYERS, MAX_POINTS_PER_SERIES);
    }

    public static List<ChartSeries> buildChartSeries(
            List<BalanceHistoryPoint> history,
            int maxPlayers,
            int maxPointsPerSeries
    ) {
        if (history == null || history.isEmpty() || maxPlayers <= 0 || maxPointsPerSeries <= 0) {
            return List.of();
        }

        Map<String, List<BalanceHistoryPoint>> byPlayer = new LinkedHashMap<>();
        for (BalanceHistoryPoint point : history) {
            byPlayer.computeIfAbsent(point.username(), ignored -> new ArrayList<>()).add(point);
        }

        List<String> topPlayers = byPlayer.entrySet().stream()
                .sorted(Comparator.comparingInt(
                        (Map.Entry<String, List<BalanceHistoryPoint>> entry) -> latestBalance(entry.getValue())
                ).reversed())
                .limit(maxPlayers)
                .map(Map.Entry::getKey)
                .toList();

        List<ChartSeries> series = new ArrayList<>(topPlayers.size());
        for (int i = 0; i < topPlayers.size(); i++) {
            String username = topPlayers.get(i);
            List<BalanceHistoryPoint> playerPoints = byPlayer.get(username);
            playerPoints.sort(Comparator.comparing(BalanceHistoryPoint::time));
            List<ChartPoint> chartPoints = downsample(playerPoints, maxPointsPerSeries).stream()
                    .map(point -> new ChartPoint(point.time().toEpochMilli(), point.money()))
                    .toList();
            series.add(new ChartSeries(username, SERIES_COLORS[i % SERIES_COLORS.length], chartPoints));
        }
        return List.copyOf(series);
    }

    static int latestBalance(List<BalanceHistoryPoint> points) {
        if (points.isEmpty()) {
            return 0;
        }
        BalanceHistoryPoint latest = points.get(points.size() - 1);
        for (BalanceHistoryPoint point : points) {
            if (!point.time().isBefore(latest.time())) {
                latest = point;
            }
        }
        return latest.money();
    }

    static List<BalanceHistoryPoint> downsample(List<BalanceHistoryPoint> points, int maxPoints) {
        if (points.size() <= maxPoints) {
            return List.copyOf(points);
        }
        List<BalanceHistoryPoint> sampled = new ArrayList<>(maxPoints);
        for (int i = 0; i < maxPoints; i++) {
            int index = (int) Math.round(i * (points.size() - 1) / (double) (maxPoints - 1));
            sampled.add(points.get(index));
        }
        return sampled;
    }
}
