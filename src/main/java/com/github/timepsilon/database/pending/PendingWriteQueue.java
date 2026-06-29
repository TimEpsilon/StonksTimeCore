package com.github.timepsilon.database.pending;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public final class PendingWriteQueue<T> {

    private final String tableName;
    private final Function<T, String> keyFn;
    private final BinaryOperator<T> mergeFn;
    private final LinkedHashMap<String, T> pending = new LinkedHashMap<>();

    public PendingWriteQueue(String tableName, Function<T, String> keyFn, BinaryOperator<T> mergeFn) {
        this.tableName = tableName;
        this.keyFn = keyFn;
        this.mergeFn = mergeFn;
    }

    public String tableName() {
        return tableName;
    }

    public boolean isEmpty() {
        return pending.isEmpty();
    }

    public int size() {
        return pending.size();
    }

    public void enqueueAll(Collection<T> entries) {
        if (entries.isEmpty()) return;
        for (T entry : entries) {
            pending.merge(keyFn.apply(entry), entry, mergeFn);
        }
    }

    public List<T> drainAll() {
        List<T> drained = new ArrayList<>(pending.values());
        pending.clear();
        return drained;
    }

    public void replaceAll(Collection<T> entries) {
        pending.clear();
        enqueueAll(entries);
    }

    public List<T> snapshot() {
        return new ArrayList<>(pending.values());
    }
}
