public class Database {

    private HikariDataSource ds;

    public void connect() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:postgresql://localhost:5432/minecraft");
        config.setUsername("postgres");
        config.setPassword("password");

        config.setMaximumPoolSize(10);

        ds = new HikariDataSource(config);

        createTable();
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void close() {
        if (ds != null) ds.close();
    }

    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS player_stats_snapshot (
                id BIGSERIAL PRIMARY KEY,
                uuid UUID NOT NULL,
                ts TIMESTAMP DEFAULT NOW(),
                play_time BIGINT,
                balance BIGINT,
                kills INT,
                deaths INT,
                mob_kills INT
            );
        """;

        try (Connection c = getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}