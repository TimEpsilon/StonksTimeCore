package com.github.timepsilon.events;

import com.github.timepsilon.commands.equivalency.GenerateEquivalency;
import com.github.timepsilon.datamaps.DataMaps;
import com.github.timepsilon.datamaps.SCTMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.awt.*;

public class NeoForgeEventsManager {

    public static final Color COLOR = Color.decode("#4f913f");

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
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
    public void onRegisterCommands(RegisterCommandsEvent event) {

        GenerateEquivalency.register(event.getDispatcher());
    }
}
