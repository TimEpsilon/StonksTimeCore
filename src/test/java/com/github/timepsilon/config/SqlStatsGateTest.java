package com.github.timepsilon.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlStatsGateTest {

    @AfterEach
    void tearDown() {
        SqlStatsGate.clearTestOverride();
    }

    @Test
    void isDisabledWhenTestOverrideIsFalse() {
        SqlStatsGate.setTestOverride(false);
        assertFalse(SqlStatsGate.isEnabled());
    }

    @Test
    void isEnabledWhenTestOverrideIsTrue() {
        SqlStatsGate.setTestOverride(true);
        assertTrue(SqlStatsGate.isEnabled());
    }
}
