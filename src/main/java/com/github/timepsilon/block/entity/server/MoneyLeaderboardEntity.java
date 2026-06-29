package com.github.timepsilon.block.entity.server;

import com.github.timepsilon.block.custom.MoneyLeaderboard;
import com.github.timepsilon.leaderboard.LeaderboardEntry;
import com.github.timepsilon.leaderboard.MoneyLeaderboardService;
import com.mojang.authlib.GameProfile;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import dev.ithundxr.createnumismatics.content.backend.BankSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MoneyLeaderboardEntity extends BlockEntity {

    private static final String ENTRIES_TAG = "Entries";
    private static final int UPDATE_INTERVAL_TICKS = 100;

    private final List<LeaderboardEntry> entries = new ArrayList<>();
    private int tickCounter;

    public MoneyLeaderboardEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public List<LeaderboardEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide && level.getServer() != null) {
            refreshLeaderboard(level.getServer());
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MoneyLeaderboardEntity entity) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        entity.tickCounter++;
        if (entity.tickCounter < UPDATE_INTERVAL_TICKS) {
            return;
        }
        entity.tickCounter = 0;
        entity.refreshLeaderboard(serverLevel.getServer());
    }

    private void refreshLeaderboard(MinecraftServer server) {
        Map<UUID, BankAccount> accounts = BankSavedData.load(server).getAccounts();
        List<MoneyLeaderboardService.AccountSnapshot> snapshots = accounts.entrySet().stream()
                .map(entry -> new MoneyLeaderboardService.AccountSnapshot(
                        resolveUsername(server, entry.getKey()),
                        entry.getValue().getBalance()
                ))
                .toList();

        List<LeaderboardEntry> updated = MoneyLeaderboardService.computeTopN(
                snapshots,
                MoneyLeaderboardService.DEFAULT_DISPLAY_COUNT
        );

        if (updated.equals(entries)) {
            return;
        }

        entries.clear();
        entries.addAll(updated);
        setChanged();
    }

    private static String resolveUsername(MinecraftServer server, UUID playerId) {
        ServerPlayer online = server.getPlayerList().getPlayer(playerId);
        if (online != null) {
            return online.getGameProfile().getName();
        }
        Optional<GameProfile> profile = server.getProfileCache().get(playerId);
        return profile.map(GameProfile::getName).orElseGet(() -> playerId.toString().substring(0, 8));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        writeEntries(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        readEntries(tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        writeEntries(tag);
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

    private void writeEntries(CompoundTag tag) {
        ListTag list = new ListTag();
        for (LeaderboardEntry entry : entries) {
            list.add(StringTag.valueOf(entry.serialize()));
        }
        tag.put(ENTRIES_TAG, list);
    }

    private void readEntries(CompoundTag tag) {
        entries.clear();
        if (!tag.contains(ENTRIES_TAG)) {
            return;
        }
        ListTag list = tag.getList(ENTRIES_TAG, 8);
        for (int i = 0; i < list.size(); i++) {
            entries.add(LeaderboardEntry.deserialize(list.getString(i)));
        }
    }
}
