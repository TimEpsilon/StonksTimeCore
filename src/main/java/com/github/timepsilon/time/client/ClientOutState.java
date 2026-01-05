package com.github.timepsilon.time.client;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientOutState {
    // On the client this is toggled on or off, allowing for the shader to be applied
    // A bit ugly but works? I'm out of ideas on how to do it
    public static boolean IS_OUT = false;

    public static Set<UUID> PLAYERS_ARE_OUT = new HashSet<>();

    public static boolean canClientSeePlayer(UUID uuid) {
        return PLAYERS_ARE_OUT.contains(uuid);
    }


}
