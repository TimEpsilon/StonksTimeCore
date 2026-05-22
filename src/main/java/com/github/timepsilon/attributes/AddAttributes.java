package com.github.timepsilon.attributes;

import com.github.timepsilon.Core;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

import static com.github.timepsilon.attributes.ModAttributes.SCT_FACTOR;

@EventBusSubscriber(modid = Core.MODID)
public class AddAttributes {

    @SubscribeEvent
    public static void modifyDefaultAttributes(EntityAttributeModificationEvent event) {
        if (!event.has(EntityType.PLAYER, SCT_FACTOR)) {
            event.add(
                    EntityType.PLAYER,
                    SCT_FACTOR,
                    1
            );
        }
    }
}
