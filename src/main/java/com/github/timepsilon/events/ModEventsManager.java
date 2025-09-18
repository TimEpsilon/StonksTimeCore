package com.github.timepsilon.events;

import com.github.timepsilon.Core;
import com.github.timepsilon.datamaps.DataMaps;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public class ModEventsManager {

    // Data Maps
    @SubscribeEvent // on the mod event bus
    public void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(DataMaps.SCT_MAP);
        Core.LOGGER.info("Registered SCT Map");
    }

}
