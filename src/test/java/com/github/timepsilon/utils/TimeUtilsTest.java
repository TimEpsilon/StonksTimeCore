package com.github.timepsilon.utils;

import com.github.timepsilon.utils.TimeCore;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeUtilsTest {

    @Test
    void formatMoneyUsesKiloSuffix() {
        assertEquals("2.45k", TimeCore.formatMoney(2450));
        assertEquals("-2.45k", TimeCore.formatMoney(-2450));
    }

    @Test
    void formatMoneyUsesMegaSuffix() {
        assertEquals("12.30m", TimeCore.formatMoney(12_300_000));
    }

    @Test
    void formatMoneyUsesMilliardSuffix() {
        assertEquals("1.00md", TimeCore.formatMoney(1_000_000_000));
    }

    @Test
    void formatMoneyUsesSpaceSeparatorsBelowThousand() {
        assertEquals("999", TimeCore.formatMoney(999));
        assertEquals("1.23m", TimeCore.formatMoney(1_234_567));
        assertEquals("-1.23m", TimeCore.formatMoney(-1_234_567));
    }

    @Test
    void secondsToTimeFormatsUnderOneDay() {
        assertEquals("0:00:00", TimeCore.secondsToTime(0));
        assertEquals("1:01:01", TimeCore.secondsToTime(3661));
        assertEquals("23:59:59", TimeCore.secondsToTime(86399));
    }

    @Test
    void secondsToTimeFormatsBeyondOneDayAsHours() {
        // Since "Redid timer rendering", durations >= 24h keep counting hours (no day suffix).
        assertEquals("24:00:00", TimeCore.secondsToTime(86400));
        assertEquals("28:35:00", TimeCore.secondsToTime(102900));
        assertEquals("60:30:00", TimeCore.secondsToTime(2 * 86400 + 12 * 3600 + 30 * 60));
    }

    @Test
    void truncateToHourStripsSubHourParts() {
        Instant input = Instant.parse("2026-06-29T12:34:56.789Z");
        Instant expected = Instant.parse("2026-06-29T12:00:00.000Z");

        assertEquals(expected, TimeCore.truncateToHour(input));
    }

    @Test
    void getCurrentHourStartIsTruncatedToHour() {
        Instant hourStart = TimeCore.getCurrentHourStart();
        assertEquals(hourStart, hourStart.truncatedTo(ChronoUnit.HOURS));
        assertEquals(0, hourStart.getNano());
        assertEquals(0L, hourStart.getEpochSecond() % 3600);
    }
}