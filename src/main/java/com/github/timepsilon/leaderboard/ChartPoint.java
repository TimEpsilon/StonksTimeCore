package com.github.timepsilon.leaderboard;

public record ChartPoint(long epochMillis, int money) {

    public String serialize() {
        return epochMillis + "\0" + money;
    }

    public static ChartPoint deserialize(String raw) {
        String[] parts = raw.split("\0", 2);
        if (parts.length < 2) {
            return new ChartPoint(0, 0);
        }
        return new ChartPoint(Long.parseLong(parts[0]), Integer.parseInt(parts[1]));
    }
}
