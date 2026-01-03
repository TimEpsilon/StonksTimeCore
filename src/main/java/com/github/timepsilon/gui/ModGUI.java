package com.github.timepsilon.gui;

import com.github.timepsilon.Core;
import com.mojang.blaze3d.systems.RenderSystem;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public enum ModGUI implements ScreenElement {
    STONKS_TEMPORAL_CHRONOSCOPE("stonks_temporal_chronoscope", 200,110)
    ;

    public final ResourceLocation location;
    public int width, height;
    public int startX, startY;

    ModGUI(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    ModGUI(String location, int startX, int startY, int width, int height) {
        this(Core.MODID, location, startX, startY, width, height);
    }

    ModGUI(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y) {
        bind();
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }
}
