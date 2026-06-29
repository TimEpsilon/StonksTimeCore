package com.github.timepsilon.database.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BankEntryTest {

    private static final UUID PLAYER = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final Instant TIME = Instant.parse("2026-06-29T12:00:00.000Z");

    @Test
    void bindToDoesNotThrowAndSetsParameters() throws SQLException {
        BankEntry entry = new BankEntry(PLAYER, "Alice", TIME, 42_000);
        RecordingStatement recording = new RecordingStatement();

        assertDoesNotThrow(() -> entry.bindTo(recording.asPreparedStatement()));

        assertEquals(PLAYER, recording.objects.get(1));
        assertEquals("Alice", recording.strings.get(2));
        assertEquals(OffsetDateTime.ofInstant(TIME, ZoneOffset.UTC), recording.objects.get(3));
        assertEquals(42_000, recording.ints.get(4));
    }

    @Test
    void snapshotUsesCurrentInstantAndBalance() {
        BankEntry entry = BankEntry.snapshot(PLAYER, "Bob", 100);

        assertEquals(PLAYER, entry.player());
        assertEquals("Bob", entry.username());
        assertEquals(100, entry.money());
        assertDoesNotThrow(entry.time()::getEpochSecond);
    }

    private static final class RecordingStatement implements InvocationHandler {

        final Map<Integer, Object> objects = new HashMap<>();
        final Map<Integer, Integer> objectTypes = new HashMap<>();
        final Map<Integer, String> strings = new HashMap<>();
        final Map<Integer, Integer> ints = new HashMap<>();

        PreparedStatement asPreparedStatement() {
            return (PreparedStatement) Proxy.newProxyInstance(
                    PreparedStatement.class.getClassLoader(),
                    new Class[]{PreparedStatement.class},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "setObject" -> {
                    int index = (Integer) args[0];
                    objects.put(index, args[1]);
                    if (args.length == 3) {
                        objectTypes.put(index, (Integer) args[2]);
                    }
                    yield null;
                }
                case "setString" -> {
                    strings.put((Integer) args[0], (String) args[1]);
                    yield null;
                }
                case "setInt" -> {
                    ints.put((Integer) args[0], (Integer) args[1]);
                    yield null;
                }
                default -> defaultReturn(method.getReturnType());
            };
        }

        private static Object defaultReturn(Class<?> returnType) {
            if (returnType == boolean.class) return false;
            if (returnType == int.class) return 0;
            if (returnType == long.class) return 0L;
            return null;
        }
    }
}
