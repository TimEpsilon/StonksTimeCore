package com.github.timepsilon.datamaps;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SCTPriceRefresherTest {

    // Same formula as SCTMathUtils.currentPrice with the default config (2000, 0.1), which is not loaded in tests
    private static final BiFunction<Float, Integer, Float> FORMULA =
            (p, n) -> (float) (p * Math.pow(2, -n / (2000 / p)) * 0.9 + 0.1 * p);

    private final Map<String, Float> prices = new HashMap<>(Map.of("stone", 100f));
    private final Map<String, Integer> amounts = new HashMap<>();

    private void refresh(List<String> items, Map<String, Integer> sold, float base) {
        SCTPriceRefresher.refresh(items, sold, item -> base, FORMULA, prices, amounts);
    }

    @Test
    void duplicatesAndRepeatedRefreshesDoNotCompound() {
        refresh(List.of("stone", "stone", "stone"), Map.of("stone", 64), 100f);
        refresh(List.of("stone"), Map.of("stone", 64), 100f);

        assertEquals(19.79f, prices.get("stone"), 0.01f);
        assertEquals(64, amounts.get("stone"));
    }

    @Test
    void priceNeverFallsBelowTheLimit() {
        for (int i = 0; i < 10; i++) refresh(List.of("stone"), Map.of("stone", 100_000), 100f);

        assertEquals(10f, prices.get("stone"), 0.01f);
    }

    @Test
    void priceRecoversOnceSalesExpire() {
        refresh(List.of("stone"), Map.of("stone", 64), 100f);
        refresh(List.of("stone"), Map.of("stone", 0), 100f);

        assertEquals(100f, prices.get("stone"), 0.01f);
        assertEquals(0, amounts.get("stone"));
    }

    @Test
    void randomValueItemsAreNotReduced() {
        prices.put("stone", -1f);
        refresh(List.of("stone"), Map.of("stone", 64), -1f);

        assertEquals(-1f, prices.get("stone"));
    }

    @Test
    void itemsWithoutAPriceAreIgnored() {
        refresh(List.of("dirt"), Map.of("dirt", 64), 100f);

        assertFalse(prices.containsKey("dirt"));
        assertFalse(amounts.containsKey("dirt"));
    }
}
