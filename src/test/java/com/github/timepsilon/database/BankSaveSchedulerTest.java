package com.github.timepsilon.database;

import com.github.timepsilon.config.SqlStatsGate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankSaveSchedulerTest {

    @AfterEach
    void tearDown() {
        BankSaveScheduler.stop();
        SqlStatsGate.clearTestOverride();
    }

    @Test
    void resolveIntervalSecondsClampsToMinimumOne() {
        assertEquals(1, BankSaveScheduler.resolveIntervalSeconds(0));
        assertEquals(1, BankSaveScheduler.resolveIntervalSeconds(-10));
        assertEquals(1, BankSaveScheduler.resolveIntervalSeconds(1));
        assertEquals(30, BankSaveScheduler.resolveIntervalSeconds(30));
    }

    @Test
    void shouldStartFollowsSqlStatsGate() {
        SqlStatsGate.setTestOverride(false);
        assertFalse(BankSaveScheduler.shouldStart());

        SqlStatsGate.setTestOverride(true);
        assertTrue(BankSaveScheduler.shouldStart());
    }
}
