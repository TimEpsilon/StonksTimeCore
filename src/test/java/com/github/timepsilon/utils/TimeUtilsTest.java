package com.github.timepsilon.utils;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeUtilsTest {

    @Test
    void formatMoneyUsesKiloSuffix() {
        assertEquals("2.45k", TimeUtils.formatMoney(2450));
        assertEquals("-2.45k", TimeUtils.formatMoney(-2450));
    }

    @Test
    void formatMoneyUsesMegaSuffix() {
        assertEquals("12.30m", TimeUtils.formatMoney(12_300_000));
    }

    @Test
    void formatMoneyUsesMilliardSuffix() {
        assertEquals("1.00md", TimeUtils.formatMoney(1_000_000_000));
    }

    @Test
    void formatMoneyUsesSpaceSeparatorsBelowThousand() {
        assertEquals("999", TimeUtils.formatMoney(999));
        assertEquals("1 234 567", TimeUtils.formatMoney(1_234_567));
        assertEquals("-1 234 567", TimeUtils.formatMoney(-1_234_567));
    }

    @Test
    void secondsToTimeFormatsUnderOneDay() {
        assertEquals("0:00:00", TimeUtils.secondsToTime(0));
        assertEquals("1:01:01", TimeUtils.secondsToTime(3661));
        assertEquals("23:59:59", TimeUtils.secondsToTime(86399));
    }

    @Test
    void secondsToTimeFormatsOneDayOrMore() {
        assertEquals("1J 0:00", TimeUtils.secondsToTime(86400));
        assertEquals("1J 4:35", TimeUtils.secondsToTime(102900));
        assertEquals("2J 12:30", TimeUtils.secondsToTime(2 * 86400 + 12 * 3600 + 30 * 60));
    }

    @Test
    void truncateToHourStripsSubHourParts() {
        Instant input = Instant.parse("2026-06-29T12:34:56.789Z");
        Instant expected = Instant.parse("2026-06-29T12:00:00.000Z");

        assertEquals(expected, TimeUtils.truncateToHour(input));
    }

    @Test
    void getCurrentHourStartIsTruncatedToHour() {
        Instant hourStart = TimeUtils.getCurrentHourStart();
        assertEquals(hourStart, hourStart.truncatedTo(ChronoUnit.HOURS));
        assertEquals(0, hourStart.getNano());
        assertEquals(0L, hourStart.getEpochSecond() % 3600);
    }
}