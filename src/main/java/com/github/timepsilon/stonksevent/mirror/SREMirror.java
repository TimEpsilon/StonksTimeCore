package com.github.timepsilon.stonksevent.mirror;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.mobeffect.ModMobEffects;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = Core.MODID)
public class SREMirror extends AbstractRandomStonksEvent {

    public SREMirror(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {
        player.addEffect(new MobEffectInstance(
                ModMobEffects.MIRROR,
                STCConfigServer.CONFIG.SRE_MIRROR_DURATION.getAsInt()*20
        ));
    }

    @Override
    public void onStop(@Nullable Player player) {

    }

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getEffect() != ModMobEffects.MIRROR) return;
        if (event.getCure() == null) return;
        event.setCanceled(true);
    }
}
