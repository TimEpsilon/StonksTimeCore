package com.github.timepsilon.datamaps;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Recomputes the current prices from the amounts sold. Kept free of Minecraft classes so it can be unit tested.
 */
final class SCTPriceRefresher {

    private SCTPriceRefresher() {}

    /**
     * The current price is always derived from the <b>base</b> price, never from the previous current price:
     * refreshing several times gives the same result, and the price recovers once old sales expire.
     * <p>
     * Items missing from {@code sold} or {@code prices} are left untouched. Items with a base price of 0 or less
     * (random value) are never reduced.
     */
    static <K> void refresh(Collection<K> items,
                            Map<String, Integer> sold,
                            Function<K, Float> basePrice,
                            BiFunction<Float, Integer, Float> reducedPrice,
                            Map<K, Float> prices,
                            Map<K, Integer> amounts) {
        for (K item : new LinkedHashSet<>(items)) {
            Integer amount = sold.get(item.toString());
            if (amount == null || !prices.containsKey(item)) continue;

            amounts.put(item, amount);

            Float base = basePrice.apply(item);
            if (base == null || base <= 0) continue;
            prices.put(item, reducedPrice.apply(base, amount));
        }
    }
}
