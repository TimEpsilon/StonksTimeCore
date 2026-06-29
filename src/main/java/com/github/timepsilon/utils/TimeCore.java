package com.github.timepsilon.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Pure time/money formatting helpers with no NeoForge or config dependencies.
 * Safe to use from unit tests and database code without loading {@link TimeUtils}.
 */
public final class TimeCore {

    private TimeCore() {}

    public static String secondsToTime(int seconds) {
        if (seconds >= 86400) {
            int days = seconds / 86400;
            int remaining = seconds % 86400;
            int h = remaining / 3600;
            int m = (remaining % 3600) / 60;
            return String.format("%dJ %d:%02d", days, h, m);
        }
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        return String.format("%d:%02d:%02d", h, m, s);
    }

    public static String formatMoney(int amount) {
        long abs = Math.abs((long) amount);
        String sign = amount < 0 ? "-" : "";

        if (abs >= 1_000_000_000L) {
            return sign + formatCompactMoney(abs, 1_000_000_000L, "md");
        }
        if (abs >= 1_000_000L) {
            return sign + formatCompactMoney(abs, 1_000_000L, "m");
        }
        if (abs >= 1_000L) {
            return sign + formatCompactMoney(abs, 1_000L, "k");
        }
        return sign + formatMoneyWithSpaces(abs);
    }

    public static Instant getCurrentInstant() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

    public static Instant truncateToHour(Instant instant) {
        return instant.truncatedTo(ChronoUnit.HOURS);
    }

    public static Instant getCurrentHourStart() {
        return truncateToHour(getCurrentInstant());
    }

    private static String formatMoneyWithSpaces(long value) {
        return String.format(Locale.US, "%,d", value).replace(',', ' ');
    }

    private static String formatCompactMoney(long value, long divisor, String suffix) {
        double compact = (double) value / divisor;
        return String.format(Locale.US, "%.2f", compact) + suffix;
    }
}