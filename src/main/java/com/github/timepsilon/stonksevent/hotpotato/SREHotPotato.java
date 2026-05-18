package com.github.timepsilon.stonksevent.hotpotato;

import com.github.timepsilon.Core;
import com.github.timepsilon.mobeffect.ModMobEffects;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import static com.github.timepsilon.stonksevent.hotpotato.HotPotatoEffect.HOT_POTATO_DAMAGE;

@EventBusSubscriber(modid = Core.MODID)
public class SREHotPotato extends AbstractRandomStonksEvent {

    public SREHotPotato(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    public static final int DURATION = 10*60; // 10min

    @Override
    public void onStart(Player player) {
        playerTagsPlayer(null, player);
    }

    @Override
    public void onStop(Player player) {

    }

    @SubscribeEvent
    public static void onPlayerTag(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer receiver)) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer sender)) return;
        if (!sender.hasEffect(ModMobEffects.HOT_POTATO)) return;
        if (receiver.hasEffect(ModMobEffects.HOT_POTATO)) return;
        // Can't tag someone that is tagged

        playerTagsPlayer(sender, receiver);
    }

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getEffect() != ModMobEffects.HOT_POTATO) return;
        if (event.getCure() == null) return;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEffectExpire(MobEffectEvent.Expired event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getEffectInstance().getEffect() !=  ModMobEffects.HOT_POTATO) return;

        DamageSource source = new DamageSource(
                player.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(HOT_POTATO_DAMAGE)
        );
        // kills the player if time runs out
        player.hurt(source, Float.MAX_VALUE);
    }

    public static void playerTagsPlayer(Player prevPlayer, Player newPlayer) {

        int duration = DURATION;
        if ((prevPlayer != null) && prevPlayer.getEffect(ModMobEffects.HOT_POTATO) != null) {
            duration = prevPlayer.getEffect(ModMobEffects.HOT_POTATO).getDuration()/20;
            prevPlayer.removeEffect(ModMobEffects.HOT_POTATO);
        }

        newPlayer.addEffect(new MobEffectInstance(
                ModMobEffects.HOT_POTATO,
                20 * duration,
                0,
                true,
                true,
                true
        ));

    }
}
