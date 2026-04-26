package com.github.timepsilon.stonksevent.lifelink;

import com.github.timepsilon.Core;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import com.github.timepsilon.stonksevent.StonksEventManager;
import com.github.timepsilon.stonksevent.StonksEventType;
import com.github.timepsilon.time.PlayerOutData;
import com.github.timepsilon.utils.TimeUtils;
import dev.ithundxr.createnumismatics.Numismatics;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerHeartTypeEvent;

import java.util.Arrays;

import static com.github.timepsilon.stonksevent.StonksEventManager.isEventRunning;
import static com.github.timepsilon.stonksevent.lifelink.ModHeartTypes.LIFELINK_HEART;

@EventBusSubscriber(modid = Core.MODID)
public class SRELifeLink extends AbstractRandomStonksEvent {

    public SRELifeLink(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    private static final int DURATION = 60*60; // 1h in seconds

    @Override
    public void onStart(Player player) {
        StonksEventManager.addEvent(player, this, DURATION);
    }

    @Override
    public void onStop(Player player) {
        StonksEventManager.removeEvent(player, this);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getSource().is(DamageTypes.GENERIC_KILL)) return;
        if (!isEventRunning(StonksEventType.LIFELINK)) return;

        PlayerOutData timer = PlayerOutData.getPlayerOutData(player.getServer());
        boolean isOut = timer.isOut(player.getUUID());

        player.sendSystemMessage(Component.translatable("rse.stonkstimecore.lifelink.you_died").withStyle(ChatFormatting.RED));
        if (!isOut) {
            BankAccount account = Numismatics.BANK.getOrCreateAccount(player.getUUID(), BankAccount.Type.PLAYER);
            account.deduct((int) (account.getBalance()* TimeUtils.DEATH_LOSS));
        }

        for (Player p : player.getServer().getPlayerList().getPlayers()) {
            p.sendSystemMessage(Component.translatable("rse.stonkstimecore.lifelink.everyone_dies", player.getName().getString()).withStyle(ChatFormatting.DARK_RED));
            if (!p.isDeadOrDying()) {
                p.kill();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderHealth(PlayerHeartTypeEvent event) {
        event.setType(LIFELINK_HEART.getValue());
    }
}
