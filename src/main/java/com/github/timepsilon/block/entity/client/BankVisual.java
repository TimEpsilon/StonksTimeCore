package com.github.timepsilon.block.entity.client;

import com.github.timepsilon.block.entity.server.BankBlockEntity;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.function.Consumer;

public class BankVisual extends KineticBlockEntityVisual<BankBlockEntity> {

    protected RotatingInstance instance;
    protected Direction sourceFacing;

    public BankVisual(VisualizationContext context, BankBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        updateSourceFacing();

        var instancer = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF));

        instance = instancer.createInstance();

        instance.setup(blockEntity, Direction.Axis.Y, getSpeed())
                .setPosition(getVisualPosition())
                .rotateToFace(Direction.SOUTH, Direction.DOWN) // fixed orientation
                .setChanged();
    }

    private float getSpeed() {
        float speed = blockEntity.getSpeed();

        if (speed != 0 && sourceFacing != null) {
            if (sourceFacing == Direction.DOWN)
                speed *= 1;
            else if (sourceFacing == Direction.UP)
                speed *= -1;
        }
        return speed;
    }

    protected void updateSourceFacing() {
        if (blockEntity.hasSource()) {
            BlockPos source = blockEntity.source.subtract(pos);
            sourceFacing = Direction.getNearest(source.getX(), source.getY(), source.getZ());
        } else {
            sourceFacing = null;
        }
    }

    @Override
    public void update(float pt) {
        updateSourceFacing();
        instance.setup(blockEntity, Direction.Axis.Y, getSpeed())
                .setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(instance);
    }

    @Override
    protected void _delete() {
        instance.delete();
        instance = null;
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(instance);
    }
}
