package com.github.timepsilon.leaderboard;

import com.github.timepsilon.utils.TimeUtils;
import net.minecraft.network.chat.Component;

public record LeaderboardEntry(int rank, String username, int balance) {

    public Component toDisplayLine() {
        return Component.literal("#" + rank + " " + username + "  " + TimeUtils.formatMoney(balance) + "\u9000");
    }

    public String serialize() {
        return rank + "\0" + username + "\0" + balance;
    }

    public static LeaderboardEntry deserialize(String raw) {
        String[] parts = raw.split("\0", 3);
        if (parts.length < 3) {
            return new LeaderboardEntry(0, "", 0);
        }
        return new LeaderboardEntry(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2]));
    }
}
