public class StatsService {

    private final Database db;

    private final Map<UUID, PlayerStats> cache = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public StatsService(Database db) {
        this.db = db;
    }

    public PlayerStats get(UUID uuid) {
        return cache.computeIfAbsent(uuid, PlayerStats::new);
    }

    // --- API your mod will call ---

    public void addKill(UUID uuid) {
        get(uuid).kills++;
    }

    public void addDeath(UUID uuid) {
        get(uuid).deaths++;
    }

    public void addMobKill(UUID uuid) {
        get(uuid).mobKills++;
    }

    public void addBalance(UUID uuid, long amount) {
        get(uuid).balance += amount;
    }

    public void addPlayTime(UUID uuid, long ticks) {
        get(uuid).playTime += ticks;
    }

    // --- snapshot loop ---

    public void start() {
        scheduler.scheduleAtFixedRate(this::flush, 60, 60, TimeUnit.SECONDS);
    }

    private void flush() {
        for (PlayerStats stats : cache.values()) {
            save(stats);
        }
    }

    private void save(PlayerStats s) {
        String sql = """
            INSERT INTO player_stats_snapshot
            (uuid, play_time, balance, kills, deaths, mob_kills)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, s.uuid);
            ps.setLong(2, s.playTime);
            ps.setLong(3, s.balance);
            ps.setInt(4, s.kills);
            ps.setInt(5, s.deaths);
            ps.setInt(6, s.mobKills);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}