package com.github.timepsilon.block.entity.client;

import com.github.timepsilon.block.ModBlocks;
import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import com.github.timepsilon.create.STCPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class StonksTemporalChronoscopeVisual extends KineticBlockEntityVisual<StonksTemporalChronoscopeEntity> {

    protected final RotatingInstance shaft;
    protected final RotatingInstance ring;

    public StonksTemporalChronoscopeVisual(VisualizationContext context, StonksTemporalChronoscopeEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        BlockPos visualPos = getVisualPosition();

        shaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF, Direction.DOWN))
                .createInstance()
                .setup(blockEntity)
                .setPosition(visualPos);

        ring = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(STCPartialModels.GYROSCOPE_OUTER_RING, Direction.EAST))
                .createInstance()
                .setup(blockEntity)
                .setPosition(visualPos.getX(), visualPos.getY() + 3/16f, visualPos.getZ());

        updateGyroscope();


        shaft.setChanged();
        ring.setChanged();
    }




    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(shaft);
        consumer.accept(ring);
    }

    @Override
    public void updateLight(float partialTick) {
        relight(ring);
        relight(shaft);
    }

    @Override
    protected void _delete() {
        ring.delete();
        shaft.delete();
    }

    @Override
    public void update(float partialTick) {
        shaft.setup(blockEntity).setChanged();
        ring.setup(blockEntity).setChanged();
        updateGyroscope();
    }

    private void updateGyroscope() {
        float sign = Math.signum(blockEntity.getSpeed());
        if (Math.abs(blockEntity.getSpeed()) >= ModBlocks.STONKS_TEMPORAL_CHRONOSCOPE.get().getMinimumRequiredSpeedLevel().getSpeedValue()) {
            ring.setRotationalSpeed(7 * 6 * sign); // Speed works in increment of 6 apparently
        } else {
            ring.setRotationalSpeed(0);
        }
    }
}

