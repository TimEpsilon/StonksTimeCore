package com.github.timepsilon.events.handlers;

import com.github.timepsilon.Core;
import com.github.timepsilon.commands.STCCommand;
import com.github.timepsilon.datamaps.DataMaps;
import com.github.timepsilon.datamaps.SCTMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.awt.*;

@EventBusSubscriber(modid = Core.MODID)
public class NeoForgeEventsManager {

    public static final Color COLOR = Color.decode("#4f913f");

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        SCTMap sct = stack.getItemHolder().getData(DataMaps.SCT_MAP);
        if (sct != null) {
            event.getToolTip()
                    .add(Component.literal("SCT : ")
                            .append(Float.toString(sct.SCT()))
                            .withStyle(Style.EMPTY.withColor(COLOR.getRGB())));
        }
    }

    @SubscribeEvent
    public static void loot(LootTableLoadEvent event) {
        System.out.println(event.getTable());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        STCCommand.register(event.getDispatcher());
    }
}
