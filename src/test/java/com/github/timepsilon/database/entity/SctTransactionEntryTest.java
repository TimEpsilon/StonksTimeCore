package com.github.timepsilon.database.entity;

import com.github.timepsilon.database.SqliteHelper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SctTransactionEntryTest {

    private static final Instant TIME = Instant.parse("2026-06-29T12:00:00.000Z");
    private static final UUID PLAYER = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void bindToDoesNotThrowAndSetsParameters() throws SQLException {
        SctTransactionEntry entry = new SctTransactionEntry(TIME, PLAYER, "Alice", "minecraft:diamond", 5, 1_500);
        RecordingStatement recording = new RecordingStatement();

        assertDoesNotThrow(() -> entry.bindTo(recording.asPreparedStatement()));

        assertEquals(SqliteHelper.toIso(TIME), recording.strings.get(1));
        assertEquals(PLAYER.toString(), recording.strings.get(2));
        assertEquals("Alice", recording.strings.get(3));
        assertEquals("minecraft:diamond", recording.strings.get(4));
        assertEquals(5, recording.ints.get(5));
        assertEquals(1_500, recording.ints.get(6));
    }

    private static final class RecordingStatement implements InvocationHandler {

        final Map<Integer, Object> objects = new HashMap<>();
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
                    objects.put((Integer) args[0], args[1]);
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
