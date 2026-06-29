package com.github.timepsilon.config;

import javax.annotation.Nullable;

/**
 * Central gate for PostgreSQL analytics writes (bank snapshots, SCT transactions).
 * Gameplay data is unaffected when disabled.
 */
public final class SqlStatsGate {

    private static @Nullable Boolean testOverride;

    private SqlStatsGate() {}

    public static boolean isEnabled() {
        if (testOverride != null) {
            return testOverride;
        }
        return STCConfigServer.CONFIG.ENABLE_SQL_STATS.get();
    }

    static void setTestOverride(@Nullable Boolean enabled) {
        testOverride = enabled;
    }

    static void clearTestOverride() {
        testOverride = null;
    }
}
