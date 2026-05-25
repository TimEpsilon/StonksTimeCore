package com.github.timepsilon.stonksevent.timeless;

import com.github.timepsilon.Core;
import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.mobeffect.ModMobEffects;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerHeartTypeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.timepsilon.enumextensions.ModHeartTypes.LIFELINK_HEART;
import static com.github.timepsilon.enumextensions.ModHeartTypes.TIMELESS_HEART;
import static com.github.timepsilon.utils.TimeUtils.givenAmountOfCoins;

@EventBusSubscriber(modid = Core.MODID)
public class SRETimeless extends AbstractRandomStonksEvent {

    public SRETimeless(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {
        player.addEffect(new MobEffectInstance(
                ModMobEffects.TIMELESS,
                STCConfigServer.CONFIG.SRE_TIMELESS_DURATION.getAsInt(),
                0
        ));
    }

    @Override
    public void onStop(@Nullable Player player) {

    }

    @SubscribeEvent
    public static void onHit(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.hasEffect(ModMobEffects.TIMELESS)) return;
        // When a timeless player is hit, lose money

        int maxAmountToLose = STCConfigServer.CONFIG.SRE_TIMELESS_LOSS.getAsInt() * (player.getEffect(ModMobEffects.TIMELESS).getAmplifier() +1);
        BankAccount account = Numismatics.BANK.getOrCreateAccount(player.getUUID(), BankAccount.Type.PLAYER);
        maxAmountToLose = Math.min(maxAmountToLose, account.getBalance());

        // Lose at most 60s
        account.deduct(maxAmountToLose);

        int lootingLevel = 0;
        // looting level controls percentage of that money that gets dropped
        if (event.getSource().getEntity() != null && event.getSource().getEntity().getWeaponItem() != null) {
            ItemStack weapon = event.getSource().getEntity().getWeaponItem();
            lootingLevel = weapon.getEnchantmentLevel(player.level().holderOrThrow(Enchantments.LOOTING));
        }

        // Drop some money like Sonic
        int amountToDrop = (int) (maxAmountToLose * Math.clamp((lootingLevel+1)/4f, 0, 1));
        List<ItemStack> items = givenAmountOfCoins(amountToDrop,20);
        for (ItemStack item : items) {
            player.drop(item, true, false);
        }

    }

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getEffect() != ModMobEffects.TIMELESS) return;
        if (event.getCure() == null) return;
        event.setCanceled(true);
    }

    // Client only
    @SubscribeEvent
    public static void onRenderHealth(PlayerHeartTypeEvent event) {
        if (event.getEntity().getActiveEffectsMap().containsKey(ModMobEffects.TIMELESS)) {
            event.setType(TIMELESS_HEART.getValue());
        }
    }
}
