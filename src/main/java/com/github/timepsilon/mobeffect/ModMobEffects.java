package com.github.timepsilon.mobeffect;

import com.github.timepsilon.Core;
import com.github.timepsilon.attributes.ModAttributes;
import com.github.timepsilon.stonksevent.lifelink.LifeLinkEffect;
import com.github.timepsilon.stonksevent.hotpotato.HotPotatoEffect;
import com.github.timepsilon.stonksevent.luckysct.LuckySCTEffect;
import com.github.timepsilon.stonksevent.timeless.TimelessEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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

    public static final Holder<MobEffect> LUCKY_SCT =
            MOB_EFFECTS.register(
                    "lucky_sct", () -> new LuckySCTEffect(
                            MobEffectCategory.BENEFICIAL,
                            Color.decode("#3bde09").getRGB()
                    ).addAttributeModifier(
                            ModAttributes.SCT_FACTOR,
                            ResourceLocation.fromNamespaceAndPath(Core.MODID, "effect.lucky_sct"),
                            0.5,
                            AttributeModifier.Operation.ADD_VALUE
                    )
            );

    public static final Holder<MobEffect> TIMELESS =
            MOB_EFFECTS.register(
                    "timeless", () -> new TimelessEffect(
                            MobEffectCategory.HARMFUL,
                            Color.decode("#859ba1").getRGB()

                    )
            );

}
