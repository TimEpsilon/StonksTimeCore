package com.github.timepsilon.block.entity.server;

import com.github.timepsilon.database.MoneyDatabase;
import com.github.timepsilon.database.entity.BalanceHistoryPoint;
import com.github.timepsilon.leaderboard.ChartSeries;
import com.github.timepsilon.leaderboard.MoneyLeaderboardChartService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoneyLeaderboardEntity extends BlockEntity {

    private static final String SERIES_TAG = "Series";
    private static final int UPDATE_INTERVAL_TICKS = 100;
    private static final int HISTORY_LIMIT = 512;
    private static final int HISTORY_HOURS = 24;

    private final List<ChartSeries> chartSeries = new ArrayList<>();
    private int tickCounter;

    public MoneyLeaderboardEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public List<ChartSeries> getChartSeries() {
        return Collections.unmodifiableList(chartSeries);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            refreshChart();
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MoneyLeaderboardEntity entity) {
        if (level.isClientSide) {
            return;
        }

        entity.tickCounter++;
        if (entity.tickCounter < UPDATE_INTERVAL_TICKS) {
            return;
        }
        entity.tickCounter = 0;
        entity.refreshChart();
    }

    private void refreshChart() {
        Instant since = Instant.now().minus(HISTORY_HOURS, ChronoUnit.HOURS);
        List<BalanceHistoryPoint> history = MoneyDatabase.getDatabase().fetchBalanceHistory(since, HISTORY_LIMIT);
        List<ChartSeries> updated = MoneyLeaderboardChartService.buildChartSeries(history);

        if (updated.equals(chartSeries)) {
            return;
        }

        chartSeries.clear();
        chartSeries.addAll(updated);
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        writeSeries(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        readSeries(tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        writeSeries(tag);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private void writeSeries(CompoundTag tag) {
        ListTag list = new ListTag();
        for (ChartSeries series : chartSeries) {
            CompoundTag seriesTag = new CompoundTag();
            series.writeTo(seriesTag);
            list.add(seriesTag);
        }
        tag.put(SERIES_TAG, list);
    }

    private void readSeries(CompoundTag tag) {
        chartSeries.clear();
        if (!tag.contains(SERIES_TAG, Tag.TAG_LIST)) {
            return;
        }
        ListTag list = tag.getList(SERIES_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            chartSeries.add(ChartSeries.readFrom(list.getCompound(i)));
        }
    }
}
