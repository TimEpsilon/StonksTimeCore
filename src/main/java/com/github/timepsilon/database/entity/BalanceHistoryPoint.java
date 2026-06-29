package com.github.timepsilon.database.entity;

import java.time.Instant;

public record BalanceHistoryPoint(String username, Instant time, int money) {}
