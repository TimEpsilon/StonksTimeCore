package com.github.timepsilon.block.entity.client;

import com.github.timepsilon.block.custom.MoneyLeaderboard;
import com.github.timepsilon.block.entity.server.MoneyLeaderboardEntity;
import com.github.timepsilon.leaderboard.ChartPoint;
import com.github.timepsilon.leaderboard.ChartSeries;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

import java.util.List;

public class MoneyLeaderboardRenderer implements BlockEntityRenderer<MoneyLeaderboardEntity> {

    private static final float TEXT_SCALE = 0.012f;
    private static final int COLOR_GRID = 0x66FFFFFF;
    private static final int COLOR_AXIS = 0xFF9E9E9E;
    private static final int COLOR_BACKGROUND = 0xAA1A1A1A;

    private static final float CHART_LEFT = 0.35f;
    private static final float CHART_RIGHT = MoneyLeaderboard.WIDTH - 0.15f;
    private static final float CHART_BOTTOM = 0.35f;
    private static final float CHART_TOP = MoneyLeaderboard.HEIGHT - 1.1f;

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
        List<ChartSeries> series = be.getChartSeries();
        if (series.isEmpty()) {
            renderEmptyState(be, poseStack, buffer, packedLight);
            return;
        }

        Direction facing = be.getBlockState().getValue(MoneyLeaderboard.FACING);
        ChartBounds bounds = computeBounds(series);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(0, 0, -0.505f);

        drawFilledRect(poseStack, buffer, CHART_LEFT, CHART_BOTTOM, CHART_RIGHT, CHART_TOP, COLOR_BACKGROUND, packedLight);
        drawGrid(poseStack, buffer);
        drawSeries(poseStack, buffer, series, bounds);
        drawLegend(poseStack, buffer, series, packedLight);

        poseStack.popPose();
    }

    private void renderEmptyState(
            MoneyLeaderboardEntity be,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        Direction facing = be.getBlockState().getValue(MoneyLeaderboard.FACING);
        Font font = Minecraft.getInstance().font;
        Component message = Component.translatable("block.stonkstimecore.money_leaderboard.no_data");

        poseStack.pushPose();
        poseStack.translate(MoneyLeaderboard.WIDTH / 2f, MoneyLeaderboard.HEIGHT / 2f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(0, 0, -0.505f);
        poseStack.scale(-TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);

        Matrix4f matrix = poseStack.last().pose();
        int width = font.width(message);
        font.drawInBatch(
                message,
                -width / 2f,
                -4,
                0xFF9E9E9E,
                false,
                matrix,
                buffer,
                Font.DisplayMode.POLYGON_OFFSET,
                0,
                packedLight
        );
        poseStack.popPose();
    }

    private void drawLegend(
            PoseStack poseStack,
            MultiBufferSource buffer,
            List<ChartSeries> series,
            int packedLight
    ) {
        Font font = Minecraft.getInstance().font;
        float legendY = MoneyLeaderboard.HEIGHT - 0.55f;

        for (int i = 0; i < series.size(); i++) {
            ChartSeries chartSeries = series.get(i);
            float legendX = 0.4f + i * 2f;
            drawLine(
                    poseStack.last(),
                    buffer.getBuffer(RenderType.lines()),
                    legendX,
                    legendY,
                    legendX + 0.5f,
                    legendY,
                    chartSeries.color()
            );

            poseStack.pushPose();
            poseStack.translate(legendX + 0.65f, legendY, 0);
            poseStack.scale(-TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);

            Component label = Component.literal(chartSeries.username());
            Matrix4f matrix = poseStack.last().pose();
            font.drawInBatch(
                    label,
                    0,
                    -4,
                    chartSeries.color(),
                    false,
                    matrix,
                    buffer,
                    Font.DisplayMode.POLYGON_OFFSET,
                    0,
                    packedLight
            );
            poseStack.popPose();
        }
    }

    private void drawGrid(PoseStack poseStack, MultiBufferSource buffer) {
        VertexConsumer lines = buffer.getBuffer(RenderType.lines());
        PoseStack.Pose pose = poseStack.last();

        for (int i = 1; i < 4; i++) {
            float x = lerp(CHART_LEFT, CHART_RIGHT, i / 4f);
            drawLine(pose, lines, x, CHART_BOTTOM, x, CHART_TOP, COLOR_GRID);
        }
        for (int i = 1; i < 4; i++) {
            float y = lerp(CHART_BOTTOM, CHART_TOP, i / 4f);
            drawLine(pose, lines, CHART_LEFT, y, CHART_RIGHT, y, COLOR_GRID);
        }

        drawLine(pose, lines, CHART_LEFT, CHART_BOTTOM, CHART_RIGHT, CHART_BOTTOM, COLOR_AXIS);
        drawLine(pose, lines, CHART_LEFT, CHART_BOTTOM, CHART_LEFT, CHART_TOP, COLOR_AXIS);
    }

    private void drawSeries(
            PoseStack poseStack,
            MultiBufferSource buffer,
            List<ChartSeries> series,
            ChartBounds bounds
    ) {
        VertexConsumer lines = buffer.getBuffer(RenderType.lines());
        PoseStack.Pose pose = poseStack.last();

        for (ChartSeries chartSeries : series) {
            List<ChartPoint> points = chartSeries.points();
            for (int i = 1; i < points.size(); i++) {
                ChartPoint previous = points.get(i - 1);
                ChartPoint current = points.get(i);
                drawLine(
                        pose,
                        lines,
                        mapX(previous.epochMillis(), bounds),
                        mapY(previous.money(), bounds),
                        mapX(current.epochMillis(), bounds),
                        mapY(current.money(), bounds),
                        chartSeries.color() | 0xFF000000
                );
            }
        }
    }

    private void drawFilledRect(
            PoseStack poseStack,
            MultiBufferSource buffer,
            float left,
            float bottom,
            float right,
            float top,
            int color,
            int packedLight
    ) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.debugQuads());
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        consumer.addVertex(matrix, left, bottom, 0).setColor(r, g, b, a).setNormal(pose, 0, 0, 1);
        consumer.addVertex(matrix, right, bottom, 0).setColor(r, g, b, a).setNormal(pose, 0, 0, 1);
        consumer.addVertex(matrix, right, top, 0).setColor(r, g, b, a).setNormal(pose, 0, 0, 1);
        consumer.addVertex(matrix, left, top, 0).setColor(r, g, b, a).setNormal(pose, 0, 0, 1);
    }

    private static void drawLine(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            float x1,
            float y1,
            float x2,
            float y2,
            int color
    ) {
        Matrix4f matrix = pose.pose();
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;
        consumer.addVertex(matrix, x1, y1, 0).setColor(r, g, b, a).setNormal(pose, 0, 1, 0);
        consumer.addVertex(matrix, x2, y2, 0).setColor(r, g, b, a).setNormal(pose, 0, 1, 0);
    }

    private static float mapX(long epochMillis, ChartBounds bounds) {
        if (bounds.maxTime == bounds.minTime) {
            return (CHART_LEFT + CHART_RIGHT) / 2f;
        }
        float t = (epochMillis - bounds.minTime) / (float) (bounds.maxTime - bounds.minTime);
        return lerp(CHART_LEFT, CHART_RIGHT, t);
    }

    private static float mapY(int money, ChartBounds bounds) {
        if (bounds.maxMoney == bounds.minMoney) {
            return (CHART_BOTTOM + CHART_TOP) / 2f;
        }
        float t = (money - bounds.minMoney) / (float) (bounds.maxMoney - bounds.minMoney);
        return lerp(CHART_BOTTOM, CHART_TOP, t);
    }

    private static ChartBounds computeBounds(List<ChartSeries> series) {
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;
        int minMoney = Integer.MAX_VALUE;
        int maxMoney = Integer.MIN_VALUE;

        for (ChartSeries chartSeries : series) {
            for (ChartPoint point : chartSeries.points()) {
                minTime = Math.min(minTime, point.epochMillis());
                maxTime = Math.max(maxTime, point.epochMillis());
                minMoney = Math.min(minMoney, point.money());
                maxMoney = Math.max(maxMoney, point.money());
            }
        }

        if (minMoney == maxMoney) {
            minMoney = Math.max(0, minMoney - 1);
            maxMoney = maxMoney + 1;
        }
        if (minTime == maxTime) {
            minTime -= 60_000L;
            maxTime += 60_000L;
        }

        return new ChartBounds(minTime, maxTime, minMoney, maxMoney);
    }

    private static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    private record ChartBounds(long minTime, long maxTime, int minMoney, int maxMoney) {}
}
