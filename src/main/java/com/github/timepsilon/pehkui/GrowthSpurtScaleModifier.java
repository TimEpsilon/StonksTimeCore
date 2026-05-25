package com.github.timepsilon.pehkui;

import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.mobeffect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleModifier;

public class GrowthSpurtScaleModifier extends ScaleModifier {

    public GrowthSpurtScaleModifier() {
        super(512);
    }

    @Override
    public float modifyScale(ScaleData scaleData, float modifiedScale, float delta) {
        if (scaleData.getEntity() instanceof LivingEntity entity) {
            MobEffectInstance effect = entity.getEffect(ModMobEffects.GROWTH_SPURT);
            if (effect != null) {
                int level = effect.getAmplifier() + 1;
                return (float) (modifiedScale + level * STCConfigServer.CONFIG.SRE_GROWTH_SPURT_FACTOR.get());
            }
        }
        return modifiedScale;
    }
}
