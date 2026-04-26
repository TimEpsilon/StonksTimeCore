package com.github.timepsilon.mobeffect;

import com.github.timepsilon.Core;
import com.github.timepsilon.entity.custom.TimeGearEntity;
import com.github.timepsilon.stonksevent.lifelink.LifeLinkEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;
import java.util.function.Supplier;

public class ModMobEffects {

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Core.MODID);

    public static final Holder<MobEffect> LIFE_LINK =
            MOB_EFFECTS.register(
                    "lifelink", () -> new LifeLinkEffect(
                            MobEffectCategory.HARMFUL,
                            Color.decode("#912323").getRGB()
                    )

            );

}
