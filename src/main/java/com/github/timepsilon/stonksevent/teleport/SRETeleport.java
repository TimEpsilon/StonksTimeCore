package com.github.timepsilon.stonksevent.teleport;

import com.github.timepsilon.config.STCConfigServer;
import com.github.timepsilon.stonksevent.AbstractRandomStonksEvent;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SRETeleport extends AbstractRandomStonksEvent {

    private static final double MAX_DISTANCE = STCConfigServer.CONFIG.SRE_TP_DISTANCE.get();

    public SRETeleport(float weight, boolean isPositive, String combination, String name) {
        super(weight, isPositive, combination, name);
    }

    @Override
    public void onStart(Player player) {

        if (player.level().random.nextFloat() < 0.6) {
            boolean didFind = funnyTeleport(player);
            if (didFind) return;
        }

        standardTeleport(player);
    }

    @Override
    public void onStop(Player player) {

    }

    private static void standardTeleport(Player player) {
        ServerLevel level = (ServerLevel) player.level();
        double centerX = player.getX();
        double centerZ = player.getZ();

        RandomSource random = player.getRandom();
        double minX = centerX - MAX_DISTANCE;
        double maxX = centerX + MAX_DISTANCE;
        double minZ = centerZ - MAX_DISTANCE;
        double maxZ = centerZ + MAX_DISTANCE;

        double x = random.nextDouble() * (maxX - minX) + minX;
        double z = random.nextDouble() * (maxZ - minZ) + minZ;

        BlockPos.MutableBlockPos pos =  new BlockPos.MutableBlockPos(x, level.getMaxBuildHeight(), z);

        while (pos.getY() > level.getMinBuildHeight() && level.getBlockState(pos).isAir()) {
            pos.move(Direction.DOWN);
        }
        int y = pos.getY() + 1;

        player.teleportTo(level, x, y, z, Set.of(), player.getYRot(), player.getXRot());
    }

    private static boolean funnyTeleport(Player player) {
        ServerLevel level = (ServerLevel) player.level();

        // Getting moving trains
        List<Train> moving = new ArrayList<>();
        for (Train train : Create.RAILWAYS.trains.values()) {
            if (train.speed > 0.1) moving.add(train);
        }

        if (moving.isEmpty()) return false;

        // Tp 10 blocks in front of random train
        Train train = moving.get(level.getRandom().nextInt(moving.size()));
        TrackGraph graph = train.graph;
        if (graph == null) return false;

        TravellingPoint current = train.carriages.get(0).getLeadingPoint();

        if (current.node1 == null || current.node2 == null || current.edge == null) return false;

        // This basically searches the rail network for the position 10 blocks in front of the train
        TravellingPoint scout = new TravellingPoint(
                current.node1,
                current.node2,
                current.edge,
                current.position,
                current.upsideDown
        );

        scout.travel(
                graph,
                10,
                (g,pair) -> pair.getSecond().get(0)
        );

        Vec3 target = scout.getPosition(graph);


        // look at your doom
        Vec3 look = current.getPosition(graph).subtract(target);
        double dx = look.x;
        double dy = look.y;
        double dz = look.z;

        double distXZ = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float)(Mth.atan2(dz, dx) * (180F / Math.PI)) - 90F;
        float pitch = (float)(-(Mth.atan2(dy, distXZ) * (180F / Math.PI)));

        player.teleportTo(level, target.x, target.y, target.z, Set.of(), yaw, pitch);
        return true;
    }


}
