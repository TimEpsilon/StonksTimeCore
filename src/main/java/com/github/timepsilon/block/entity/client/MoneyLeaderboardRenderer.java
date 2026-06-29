package com.github.timepsilon.block.entity.client;

import com.github.timepsilon.block.custom.MoneyLeaderboard;
import com.github.timepsilon.block.entity.server.MoneyLeaderboardEntity;
import com.github.timepsilon.leaderboard.LeaderboardEntry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

public class MoneyLeaderboardRenderer implements BlockEntityRenderer<MoneyLeaderboardEntity> {

    private static final float TEXT_SCALE = 0.014f;
    private static final int COLOR_GOLD = 0xFFD54F;
    private static final int COLOR_BRASS = 0xE8C170;
    private static final int COLOR_DIM = 0x9E9E9E;

    public MoneyLeaderboardRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public boolean shouldRenderOffScreen(MoneyLeaderboardEntity be) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    @Override
    public void render(
            MoneyLeaderboardEntity be,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        Direction facing = be.getBlockState().getValue(MoneyLeaderboard.FACING);
        Font font = Minecraft.getInstance().font;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(0, 0, 0.505);
        poseStack.scale(-TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);

        var entries = be.getEntries();
        for (int segment = 0; segment < MoneyLeaderboard.HEIGHT; segment++) {
            int displayIndex = MoneyLeaderboard.HEIGHT - 1 - segment;
            Component line = displayIndex < entries.size()
                    ? entries.get(displayIndex).toDisplayLine()
                    : Component.literal("—");

            int color = displayIndex == 0 && displayIndex < entries.size()
                    ? COLOR_GOLD
                    : displayIndex < entries.size() ? COLOR_BRASS : COLOR_DIM;

            poseStack.pushPose();
            poseStack.translate(0, segment * (16 / TEXT_SCALE), 0);

            Matrix4f matrix = poseStack.last().pose();
            int width = font.width(line);
            font.drawInBatch(
                    line,
                    -width / 2,
                    -4,
                    color,
                    false,
                    matrix,
                    buffer,
                    Font.DisplayMode.POLYGON_OFFSET,
                    0x33000000,
                    packedLight
            );
            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
