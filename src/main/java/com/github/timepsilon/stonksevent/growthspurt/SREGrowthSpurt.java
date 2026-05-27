package com.github.timepsilon.stonksevent.growthspurt;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.mobeffect.ModMobEffects;
import com.github.timepsilon.pehkui.ModPehkuiModifier;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleTypes;

import static com.github.timepsilon.utils.TimeUtils.stackEffect;

@EventBusSubscriber(modid = Core.MODID)
public class SREGrowthSpurt extends AbstractRandomStonksEvent {

    public SREGrowthSpurt(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {
        stackEffect(player, new MobEffectInstance(
                ModMobEffects.GROWTH_SPURT,
                STCConfigServer.CONFIG.SRE_GROWTH_SPURT_DURATION.getAsInt() * 20
        ), true);
        ScaleTypes.BASE.getScaleData(player).onUpdate();
    }

    @Override
    public void onStop(@Nullable Player player) {

    }

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        if (!event.getEntity().hasEffect(ModMobEffects.GROWTH_SPURT)) return;
        ScaleTypes.BASE.getScaleData(event.getEntity()).getBaseValueModifiers().remove(ModPehkuiModifier.GROWTH_SPURT);
    }

    @SubscribeEvent
    public static void onEffectExpire(MobEffectEvent.Expired event) {
        if (!event.getEntity().hasEffect(ModMobEffects.GROWTH_SPURT)) return;
        ScaleTypes.BASE.getScaleData(event.getEntity()).getBaseValueModifiers().remove(ModPehkuiModifier.GROWTH_SPURT);
    }

}
