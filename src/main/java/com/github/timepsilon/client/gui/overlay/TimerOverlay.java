package com.github.timepsilon.client.gui.overlay;

import com.github.timepsilon.utils.TimeUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TimerOverlay implements LayeredDraw.Layer {
    public static final TimerOverlay instance = new TimerOverlay();

    private static final int ROW_HEIGHT = 24;
    private static final int ROW_SPACING = 2;
    private static final int EDGE_MARGIN = 3;
    private static final int ICON_SIZE = 18;
    private static final int ICON_PADDING = 3;
    private static final int TEXT_PADDING = 4;
    private static final int INFO_TIME = 20 * 3;

    private static final ItemStack TIME_ICON = new ItemStack(Items.CLOCK);
    private static final ItemStack MONEY_ICON = new ItemStack(Items.GOLD_NUGGET);

    private int seconds = 0;
    private int money = 0;
    private boolean isOut;
    private static final HashMap<String, Long> timeInfo = new HashMap<>();

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;
        if (minecraft.options.hideGui || minecraft.player.isSpectator()) return;
        if (isOut) return;

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        Font font = minecraft.font;

        String timeText = TimeUtils.secondsToTime(seconds);
        String moneyText = money + "\u9000";

        int panelWidth = computePanelWidth(font, timeText, moneyText);
        int panelX = screenWidth - EDGE_MARGIN - panelWidth;
        int moneyRowY = screenHeight - EDGE_MARGIN - ROW_HEIGHT;
        int timeRowY = moneyRowY - ROW_SPACING - ROW_HEIGHT;

        drawStatusRow(guiGraphics, minecraft, panelX, timeRowY, panelWidth, TIME_ICON, timeText, getColor());
        drawStatusRow(guiGraphics, minecraft, panelX, moneyRowY, panelWidth, MONEY_ICON, moneyText, 0xFFFFFF);

        if (!timeInfo.isEmpty()) {
            manageNotifications(guiGraphics, screenHeight, timeRowY, panelWidth, font, minecraft);
        }
    }

    private static int computePanelWidth(Font font, String timeText, String moneyText) {
        int contentWidth = Math.max(font.width(timeText), font.width(moneyText));
        return ROW_HEIGHT + TEXT_PADDING + contentWidth + TEXT_PADDING;
    }

    private static void drawStatusRow(GuiGraphics guiGraphics, Minecraft minecraft, int x, int y, int width,
                                      ItemStack icon, String text, int textColor) {
        TextureAtlasSprite background = minecraft.guiSprites.getSprite(Gui.EFFECT_BACKGROUND_TEXTURE);
        guiGraphics.blitSprite(background, x, y, width, ROW_HEIGHT);

        int iconX = x + ICON_PADDING;
        int iconY = y + (ROW_HEIGHT - ICON_SIZE) / 2;
        guiGraphics.renderItem(icon, iconX, iconY);

        int textX = x + ROW_HEIGHT + TEXT_PADDING;
        int textY = y + (ROW_HEIGHT - minecraft.font.lineHeight) / 2;
        guiGraphics.drawString(minecraft.font, text, textX, textY, textColor, true);
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setOut(boolean isOut) {
        this.isOut = isOut;
    }

    public boolean isOut() {
        return this.isOut;
    }

    public void addInfo(String info) {
        timeInfo.put(info, Minecraft.getInstance().level.getGameTime());
    }

    private void manageNotifications(GuiGraphics guiGraphics, int screenHeight, int timeRowY,
                                     int panelWidth, Font font, Minecraft minecraft) {
        Iterator<Map.Entry<String, Long>> iterator = timeInfo.entrySet().iterator();
        Level level = minecraft.level;
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            long currentDeltaTime = level.getGameTime() - entry.getValue();
            if (currentDeltaTime > INFO_TIME) {
                iterator.remove();
                continue;
            }

            float progress = (float) currentDeltaTime / INFO_TIME;
            String text = entry.getKey();
            int rowWidth = Math.max(panelWidth, font.width(text) + ROW_HEIGHT + TEXT_PADDING * 2);
            int rowX = guiGraphics.guiWidth() - EDGE_MARGIN - rowWidth;

            float startY = (float) timeRowY / screenHeight - 0.06f;
            float endY = startY - 0.1f;
            float relativeY = interpolateCubic(progress, startY, endY - startY);
            int notifyY = (int) (screenHeight * relativeY);

            drawNotificationRow(guiGraphics, minecraft, rowX, notifyY, rowWidth, text, getTransparentWhite(progress));
        }
    }

    private static void drawNotificationRow(GuiGraphics guiGraphics, Minecraft minecraft, int x, int y, int width,
                                              String text, int textColor) {
        TextureAtlasSprite background = minecraft.guiSprites.getSprite(Gui.EFFECT_BACKGROUND_TEXTURE);
        guiGraphics.blitSprite(background, x, y, width, ROW_HEIGHT);

        int textX = x + TEXT_PADDING;
        int textY = y + (ROW_HEIGHT - minecraft.font.lineHeight) / 2;
        guiGraphics.drawString(minecraft.font, text, textX, textY, textColor, true);
    }

    private float interpolateCubic(float x, float x0, float dx) {
        return (float) (1 - Math.pow(1 - x, 3)) * dx + x0;
    }

    private int getTransparentWhite(float x) {
        float alphaF;

        if (x < 0.75f) {
            alphaF = 1.0f;
        } else {
            alphaF = 1.0f - ((x - 0.75f) / 0.25f);
        }

        alphaF = Math.clamp(alphaF, 0.1f, 1.0f);

        int alpha = (int) (alphaF * 255.0f);
        return (alpha << 24) | 0xFFFFFF;
    }

    private int getColor() {
        if (seconds > TimeUtils.SAFE_TIME) return Color.decode("#39a32a").getRGB();
        if (seconds < TimeUtils.DANGER_TIME) return Color.decode("#c90808").getRGB();
        float t = ((float) seconds - TimeUtils.DANGER_TIME) / (TimeUtils.SAFE_TIME - TimeUtils.DANGER_TIME);

        return interpolateColor(t);
    }

    private int interpolateColor(float t) {
        int rG = 0x39, gG = 0xA3, bG = 0x2A;
        int rY = 0xEA, gY = 0xFF, bY = 0x29;
        int rR = 0xC9, gR = 0x08, bR = 0x08;

        if (t >= 0.5) {
            double u = (t - 0.5) * 2.0;
            return new Color(
                    (int) (rY + u * (rG - rY)),
                    (int) (gY + u * (gG - gY)),
                    (int) (bY + u * (bG - bY))
            ).getRGB();
        } else {
            double u = t * 2.0;
            return new Color(
                    (int) (rR + u * (rY - rR)),
                    (int) (gR + u * (gY - gR)),
                    (int) (bR + u * (bY - bR))
            ).getRGB();
        }
    }
}
