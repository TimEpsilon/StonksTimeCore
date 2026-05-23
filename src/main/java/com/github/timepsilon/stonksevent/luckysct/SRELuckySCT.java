package com.github.timepsilon.stonksevent.luckysct;

import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.mobeffect.ModMobEffects;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class SRELuckySCT extends AbstractRandomStonksEvent {

    public SRELuckySCT(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {
        player.addEffect(new MobEffectInstance(
                ModMobEffects.LUCKY_SCT,
                20* STCConfigServer.CONFIG.SRE_LUCKY_SCT_DURATION.get(),
                0,
                true,
                true,
                true
        ));
    }

    @Override
    public void onStop(@Nullable Player player) {

    }
}
