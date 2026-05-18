package com.github.timepsilon.mobeffect;

import com.github.timepsilon.Core;
import com.github.timepsilon.stonksevent.lifelink.LifeLinkEffect;
import com.github.timepsilon.stonksevent.hotpotato.HotPotatoEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;

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

    public static final Holder<MobEffect> HOT_POTATO =
            MOB_EFFECTS.register(
                    "hot_potato", () -> new HotPotatoEffect(
                            MobEffectCategory.HARMFUL,
                            Color.decode("#fabc2a").getRGB()
                    )
            );

}
