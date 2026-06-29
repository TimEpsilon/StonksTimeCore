package com.github.timepsilon.database;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.STCConfigServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class PostgresHelper {

    private PostgresHelper() {}

    public static Connection open() throws SQLException {
        return open(
                STCConfigServer.CONFIG.DB_HOST.get(),
                STCConfigServer.CONFIG.DB_PORT.get(),
                STCConfigServer.CONFIG.DB_NAME.get(),
                STCConfigServer.CONFIG.DB_USER.get(),
                STCConfigServer.CONFIG.DB_PASSWORD.get()
        );
    }

    public static Connection open(String host, int port, String database, String user, String password)
            throws SQLException {
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        Core.LOGGER.debug("Opening PostgreSQL connection: url={}, user={}", jdbcUrl, user);
        Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
        Core.LOGGER.debug("PostgreSQL connection opened: url={}, user={}", jdbcUrl, user);
        return connection;
    }
}
