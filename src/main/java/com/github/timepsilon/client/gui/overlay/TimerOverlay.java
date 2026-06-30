package com.github.timepsilon.client.gui.overlay;

import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.utils.TimeUtils;
import dev.ithundxr.createnumismatics.content.backend.Coin;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TimerOverlay implements LayeredDraw.Layer {
    public static final TimerOverlay instance = new TimerOverlay();

    private int seconds = 0;
    private int money = 0;
    private boolean isOut;
    private static final float textX = 0.9f;
    private static final float textY = 0.9f;
    private static final float deltaY = -0.1f;
    private static final int infoTime = 20*3;
    private static final HashMap<String, Long> timeInfo = new HashMap<>();

    private static final ItemStack TIME_ICON = new ItemStack(Items.CLOCK);
    private static final ItemStack MONEY_ICON = Coin.SUN.asStack();

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;
        if (minecraft.options.hideGui || minecraft.player.isSpectator()) return; // Hide timer if F1 or spectator
        if (isOut) return ; // Being out =>  no more render

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();

        String timeText = TimeUtils.secondsToTime(seconds);
        String moneyText = TimeUtils.formatMoney(money);

        guiGraphics.drawString(Minecraft.getInstance().font, TimeUtils.secondsToTime(seconds), width*textX, height*textY, getColor(), true);
        guiGraphics.drawString(Minecraft.getInstance().font, money+"\u9000", width*textX, height*(textY-0.03f), Color.WHITE.getRGB(), true);

        if (!timeInfo.isEmpty()) manageNotifications(guiGraphics, width, height);
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

    private void manageNotifications(GuiGraphics guiGraphics, int width, int height) {
        Iterator<Map.Entry<String,Long>> iterator = timeInfo.entrySet().iterator();
        Level level = Minecraft.getInstance().level;
        while (iterator.hasNext()) {
            Map.Entry<String,Long> entry = iterator.next();
            long currentDeltaTime = level.getGameTime() - entry.getValue();
            if (currentDeltaTime > infoTime) {
                iterator.remove();
                break;
            }

            float x = (float) currentDeltaTime / infoTime;
            guiGraphics.drawString(Minecraft.getInstance().font, entry.getKey(), width*textX, height*interpolateCubic(x, textY-0.06f, deltaY), getTransparentWhite(x), true);
        }
    }

    private float interpolateCubic(float x, float x0, float dx) {
        return (float) (1-Math.pow(1-x,3))*dx + x0;
    }

    private int getTransparentWhite(float x) {
        // 100% opacity for 0-0.75
        // linear descent between 0.75 and 1
        float alphaF;

        if (x < 0.75f) {
            alphaF = 1.0f;
        } else {
            alphaF = 1.0f - ((x - 0.75f) / 0.25f);
        }

        alphaF = Math.clamp(alphaF, 0.1f, 1.0f);

        int alpha = (int)(alphaF * 255.0f);
        return (alpha << 24) | 0xFFFFFF;
    }

    private int getColor() {
        // Above 6h remaining -> Green
        // Below 30min -> Red
        // In between -> Green -> Yellow -> Orange -> Red
        if (seconds > STCConfigServer.CONFIG.SAFE_TIME.getAsInt()) return Color.decode("#39a32a").getRGB();
        if (seconds < STCConfigServer.CONFIG.DANGER_TIME.getAsInt()) return Color.decode("#c90808").getRGB();
        float t = ((float)seconds - STCConfigServer.CONFIG.DANGER_TIME.getAsInt()) / (STCConfigServer.CONFIG.SAFE_TIME.getAsInt()-STCConfigServer.CONFIG.DANGER_TIME.getAsInt());

        return interpolateColor(t);
    }

    private int interpolateColor(float t) {
        int rG = 0x39, gG = 0xA3, bG = 0x2A; // green  #39A32A
        int rY = 0xEA, gY = 0xFF, bY = 0x29; // yellow #EAFF29
        int rR = 0xC9, gR = 0x08, bR = 0x08; // red    #C90808

        if (t >= 0.5) {
            double u = (t - 0.5) * 2.0; // [0,1]
            return new Color(
                    (int) (rY + u * (rG - rY)),
                    (int) (gY + u * (gG - gY)),
                    (int) (bY + u * (bG - bY))
            ).getRGB();
        } else {
            double u = t * 2.0; // [0,1]
            return new Color(
                    (int) (rR + u * (rY - rR)),
                    (int) (gR + u * (gY - gR)),
                    (int) (bR + u * (bY - bR))
            ).getRGB();
        }
    }


}
