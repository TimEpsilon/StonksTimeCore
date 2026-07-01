package com.github.timepsilon.events.handlers;

import com.github.timepsilon.Core;
import com.github.timepsilon.commands.STCCommand;
import com.github.timepsilon.config.SqlStatsGate;
import com.github.timepsilon.database.BankSaveScheduler;
import com.github.timepsilon.database.MoneyDatabase;
import com.github.timepsilon.database.SCTTransactionDatabase;
import com.github.timepsilon.database.pending.PendingWritesStore;
import com.github.timepsilon.datamaps.DataMaps;
import com.github.timepsilon.datamaps.SCTManager;
import com.github.timepsilon.datamaps.SCTMap;
import com.github.timepsilon.utils.TimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.awt.*;

@EventBusSubscriber(modid = Core.MODID)
public class NeoForgeEventsManager {

    public static final Color COLOR = Color.decode("#4f913f");
    public static final Color COLOR_TIME = Color.decode("#4a5c46");

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Float sct = SCTManager.SCT_MAPS.get(stack.getItem());
        if (sct != null) {
            if (sct < 0) {
                event.getToolTip()
                        .add(Component.literal("SCT : ")
                                .withStyle(Style.EMPTY.withColor(COLOR.getRGB()))
                                .append(Component.literal("NaN")
                                        .withStyle(Style.EMPTY.withColor(COLOR.getRGB()))
                                        .withStyle(ChatFormatting.OBFUSCATED)));
            } else {
                event.getToolTip()
                        .add(Component.literal("SCT : ")
                                .append(Float.toString(sct))
                                .withStyle(Style.EMPTY.withColor(COLOR.getRGB()))
                                .append(Component.literal(" (" + TimeUtils.SCTToTime(sct) + ")").withStyle(Style.EMPTY.withColor(COLOR_TIME.getRGB()))));
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        STCCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerLoad(ServerStartedEvent event) {
        PendingWritesStore.get().bindServer(event.getServer());
        if (!SqlStatsGate.isEnabled()) {
            Core.LOGGER.info("SQL stats disabled — skipping PostgreSQL connection and bank save scheduler.");
            return;
        }
        Core.LOGGER.info("Database Setup...");
        SCTTransactionDatabase.getDatabase().load(event.getServer());
        MoneyDatabase.getDatabase().load(event.getServer());
        BankSaveScheduler.start(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStop(ServerStoppedEvent event) {
        if (!SqlStatsGate.isEnabled()) {
            PendingWritesStore.get().clearServer();
            return;
        }
        BankSaveScheduler.stop();
        MoneyDatabase.getDatabase().saveBanks();

        Core.LOGGER.info("Database Shutdown...");
        SCTTransactionDatabase.getDatabase().unload();
        MoneyDatabase.getDatabase().unload();
        PendingWritesStore.get().clearServer();
    }
}
