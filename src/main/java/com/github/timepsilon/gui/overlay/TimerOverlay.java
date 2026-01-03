package com.github.timepsilon.gui.overlay;

import com.github.timepsilon.time.TimeManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

import java.awt.*;

public class TimerOverlay implements LayeredDraw.Layer {
    public static final TimerOverlay instance = new TimerOverlay();

    private static final int safeTime = 21600; // 6h
    private static final int dangerTime = 1800; // 30min

    private int seconds = 0;
    private int money = 0;
    private boolean isOut;
    private static final float textX = 0.9f;
    private static final float textY = 0.9f;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().options.hideGui || Minecraft.getInstance().player.isSpectator()) return; // Hide timer if F1 or spectator
        if (isOut) return ; // Being out =>  no more render

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();

        guiGraphics.drawString(Minecraft.getInstance().font, TimeManager.secondsToTime(seconds), width*textX, height*textY, getColor(), true);
        guiGraphics.drawString(Minecraft.getInstance().font, money+"\u9000", width*textX, height*(textY-0.03f), Color.WHITE.getRGB(), true);
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

    private int getColor() {
        // Above 6h remaining -> Green
        // Below 30min -> Red
        // In between -> Green -> Yellow -> Orange -> Red
        if (seconds > safeTime) return Color.decode("#39a32a").getRGB();
        if (seconds < dangerTime) return Color.decode("#c90808").getRGB();
        float t = ((float)seconds - dangerTime) / (safeTime-dangerTime);

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
