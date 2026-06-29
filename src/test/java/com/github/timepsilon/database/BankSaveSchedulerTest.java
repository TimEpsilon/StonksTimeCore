package com.github.timepsilon.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BankSaveSchedulerTest {

    @AfterEach
    void tearDown() {
        BankSaveScheduler.stop();
    }

    @Test
    void resolveIntervalSecondsClampsToMinimumOne() {
        assertEquals(1, BankSaveScheduler.resolveIntervalSeconds(0));
        assertEquals(1, BankSaveScheduler.resolveIntervalSeconds(-10));
        assertEquals(1, BankSaveScheduler.resolveIntervalSeconds(1));
        assertEquals(30, BankSaveScheduler.resolveIntervalSeconds(30));
    }
}
