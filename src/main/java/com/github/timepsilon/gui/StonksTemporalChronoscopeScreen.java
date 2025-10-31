package com.github.timepsilon.gui;

import com.github.timepsilon.block.ModBlocks;
import com.github.timepsilon.gui.packets.StonksTemporalChronoscopeMoneyPacket;
import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.redstone.thresholdSwitch.ConfigureThresholdSwitchPacket;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Collections;
import java.util.List;

public class StonksTemporalChronoscopeScreen extends AbstractSimiContainerScreen<StonksTemporalChronoscopeMenu> {

    private IconButton confirmButton;

    private final ModGUI background = ModGUI.STONKS_TEMPORAL_CHRONOSCOPE;
    private final ItemStack renderedItem = ModBlocks.STONKS_TEMPORAL_CHRONOSCOPE.asStack();
    private List<Rect2i> extraAreas = Collections.emptyList();

    public StonksTemporalChronoscopeScreen(StonksTemporalChronoscopeMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void init() {
        setWindowSize(background.width, background.height + 2 + AllGuiTextures.PLAYER_INVENTORY.getHeight());
        setWindowOffset(0, 0);
        super.init();

        int x = leftPos;
        int y = topPos;

        if (menu.contentHolder.isActive()) { // TODO : Server -> Client Packet
            confirmButton = new IconButton(x + background.width - 33, y + background.height - 24, AllIcons.I_CONFIRM);
            confirmButton.withCallback(() ->
                    CatnipServices.NETWORK.sendToServer(new StonksTemporalChronoscopeMoneyPacket(menu.contentHolder.getBlockPos()))
            );
        } else {
            confirmButton = new IconButton(x + background.width - 33, y + background.height - 24, AllIcons.I_MTD_CLOSE);
            confirmButton.active = false;
        }

        addRenderableWidget(confirmButton);

        extraAreas = ImmutableList.of(new Rect2i(x + background.width, y + background.height - 64, 84, 74));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        int invX = getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.getWidth());
        int invY = topPos + background.height + 2;
        renderPlayerInventory(guiGraphics, invX, invY);

        int x = leftPos;
        int y = topPos;

        background.render(guiGraphics, x, y);

        GuiGameElement.of(renderedItem).<GuiGameElement
                        .GuiRenderBuilder>at(x + background.width + 6, y + background.height - 70, -200)
                .scale(5)
                .render(guiGraphics);

        guiGraphics.drawString(font, title, leftPos + (background.width - 8) / 2 - font.width(title) / 2, topPos + 4, 0x592424, false);
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return extraAreas;
    }

}
