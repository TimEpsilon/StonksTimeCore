public class StatsEventHandler {

    private final StatsService stats;

    public StatsEventHandler(StatsService stats) {
        this.stats = stats;
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        stats.get(e.getEntity().getUUID());
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        // optional: keep or remove
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent e) {

        if (e.getSource().getEntity() instanceof ServerPlayer killer) {
            stats.addKill(killer.getUUID());
        }

        if (e.getEntity() instanceof ServerPlayer victim) {
            stats.addDeath(victim.getUUID());
        }
    }
}