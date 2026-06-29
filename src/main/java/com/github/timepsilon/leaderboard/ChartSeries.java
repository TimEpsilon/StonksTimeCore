package com.github.timepsilon.leaderboard;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.ArrayList;
import java.util.List;

public record ChartSeries(String username, int color, List<ChartPoint> points) {

    public void writeTo(CompoundTag tag) {
        tag.putString("username", username);
        tag.putInt("color", color);
        ListTag pointList = new ListTag();
        for (ChartPoint point : points) {
            pointList.add(StringTag.valueOf(point.serialize()));
        }
        tag.put("points", pointList);
    }

    public static ChartSeries readFrom(CompoundTag tag) {
        String username = tag.getString("username");
        int color = tag.getInt("color");
        List<ChartPoint> points = new ArrayList<>();
        ListTag pointList = tag.getList("points", 8);
        for (int i = 0; i < pointList.size(); i++) {
            points.add(ChartPoint.deserialize(pointList.getString(i)));
        }
        return new ChartSeries(username, color, List.copyOf(points));
    }
}
