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
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerHeartTypeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.timepsilon.enumextensions.ModHeartTypes.LIFELINK_HEART;
import static com.github.timepsilon.enumextensions.ModHeartTypes.TIMELESS_HEART;
import static com.github.timepsilon.utils.TimeUtils.givenAmountOfCoins;
import static com.github.timepsilon.utils.TimeUtils.stackEffect;

@EventBusSubscriber(modid = Core.MODID)
public class SRETimeless extends AbstractRandomStonksEvent {

    public SRETimeless(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {
        stackEffect(player, new MobEffectInstance(
                ModMobEffects.TIMELESS,
                STCConfigServer.CONFIG.SRE_TIMELESS_DURATION.getAsInt() * 20,
                0
        ), true);
    }

    @Override
    public void onStop(@Nullable Player player) {

    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.hasEffect(ModMobEffects.TIMELESS)) return;

        // When a timeless player is hit, lose money
        int maxAmountToLose = (int) (STCConfigServer.CONFIG.SRE_TIMELESS_LOSS.getAsInt() * (player.getEffect(ModMobEffects.TIMELESS).getAmplifier() +1) * event.getNewDamage());
        BankAccount account = Numismatics.BANK.getOrCreateAccount(player.getUUID(), BankAccount.Type.PLAYER);
        maxAmountToLose = Math.min(maxAmountToLose, account.getBalance());

        // Lose at most 60s per lost hp
        account.deduct(maxAmountToLose);

        int lootingLevel = 0;
        // looting level controls percentage of that money that gets dropped
        if (event.getSource().getEntity() != null && event.getSource().getEntity().getWeaponItem() != null) {
            ItemStack weapon = event.getSource().getEntity().getWeaponItem();
            lootingLevel = weapon.getEnchantmentLevel(player.level().holderOrThrow(Enchantments.LOOTING));
        }

        // Drop some money like Sonic
        int amountToDrop = (int) (maxAmountToLose * Math.clamp((lootingLevel+1)/4f, 0, 1));
        List<ItemStack> items = givenAmountOfCoins(amountToDrop,(int)(20 * event.getNewDamage()));
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
