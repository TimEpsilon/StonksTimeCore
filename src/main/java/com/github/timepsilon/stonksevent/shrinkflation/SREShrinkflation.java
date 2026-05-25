package com.github.timepsilon.stonksevent.shrinkflation;

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

@EventBusSubscriber(modid = Core.MODID)
public class SREShrinkflation extends AbstractRandomStonksEvent {

    public SREShrinkflation(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {
        player.addEffect(new MobEffectInstance(
                ModMobEffects.SHRINKFLATION,
                STCConfigServer.CONFIG.SRE_SHRINKFLATION_DURATION.getAsInt() * 20
        ));
    }

    @Override
    public void onStop(@Nullable Player player) {

    }

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        if (!event.getEntity().hasEffect(ModMobEffects.SHRINKFLATION)) return;
        ScaleTypes.BASE.getScaleData(event.getEntity()).getBaseValueModifiers().remove(ModPehkuiModifier.SHRINKFLATION);
    }

    @SubscribeEvent
    public static void onEffectExpire(MobEffectEvent.Expired event) {
        if (!event.getEntity().hasEffect(ModMobEffects.SHRINKFLATION)) return;
        ScaleTypes.BASE.getScaleData(event.getEntity()).getBaseValueModifiers().remove(ModPehkuiModifier.SHRINKFLATION);
    }

}
