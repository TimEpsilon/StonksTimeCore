package com.github.timepsilon.block.entity.client;

import com.github.timepsilon.block.ModBlocks;
import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import com.github.timepsilon.create.STCPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class StonksTemporalChronoscopeVisual extends KineticBlockEntityVisual<StonksTemporalChronoscopeEntity> implements SimpleDynamicVisual {

    protected final RotatingInstance shaft;
    protected final TransformedInstance ring;
    protected final TransformedInstance innerRing;
    protected final TransformedInstance timeGear;

    // Speed in rad / s
    protected final float ringIncrementAngle = (float) (2*Math.PI / 7 / 60); // Assume 60fps
    protected final float innerRingIncrementAngle = (float) (2*Math.PI / 13 / 60);
    protected final float timeGearIncrementAngle = (float) (2*Math.PI / 5 / 60);

    protected float innerRingCurrentAngle;

    public StonksTemporalChronoscopeVisual(VisualizationContext context, StonksTemporalChronoscopeEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        BlockPos visualPos = getVisualPosition();
        innerRingCurrentAngle = 0;

        shaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF, Direction.DOWN))
                .createInstance()
                .setup(blockEntity)
                .setPosition(visualPos);

        ring = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(STCPartialModels.GYROSCOPE_OUTER_RING))
                .createInstance()
                .setTransform(new PoseStack(){{ translate(visualPos.getX(), visualPos.getY() + 3/16f, visualPos.getZ()); }});

        innerRing = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(STCPartialModels.GYROSCOPE_INNER_RING))
                .createInstance()
                .setTransform(new PoseStack(){{ translate(visualPos.getX(), visualPos.getY() + 3/16f, visualPos.getZ()); }});

        timeGear = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(STCPartialModels.GYROSCOPE_TIME_GEAR))
                .createInstance()
                .setTransform(new PoseStack(){{ translate(visualPos.getX(), visualPos.getY() + 3/16f, visualPos.getZ()); }});

        shaft.setChanged();
        ring.setChanged();
        innerRing.setChanged();
        timeGear.setChanged();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(shaft);
        consumer.accept(ring);
        consumer.accept(innerRing);
        consumer.accept(timeGear);
    }

    @Override
    public void updateLight(float partialTick) {
        relight(ring);
        relight(shaft);
        relight(innerRing);
        relight(timeGear);
    }

    @Override
    protected void _delete() {
        ring.delete();
        shaft.delete();
        innerRing.delete();
        timeGear.delete();
    }

    @Override
    public void update(float partialTick) {
        shaft.setup(blockEntity).setChanged();
    }

    private void tickGyroscope() {
        float sign = Math.signum(blockEntity.getSpeed());

        if (Math.abs(blockEntity.getSpeed()) >= ModBlocks.STONKS_TEMPORAL_CHRONOSCOPE.get().getMinimumRequiredSpeedLevel().getSpeedValue()) {
            // Center origin, rotate, undo translation
            ring.translate(0.5,0,0.5);
            ring.rotate(ringIncrementAngle * sign, Axis.YP);
            ring.translateBack(0.5,0,0.5);
            ring.setChanged();

            innerRingCurrentAngle += innerRingIncrementAngle * sign;

            innerRing.setTransform(ring.pose);
            innerRing.translate(0.5,22f/16,0.5);
            innerRing.pose.rotateX(innerRingCurrentAngle);
            innerRing.translateBack(0.5,22f/16,0.5);
            innerRing.setChanged();

            timeGear.translate(0.5,0,0.5);
            timeGear.rotate(timeGearIncrementAngle * sign, Axis.YP);
            timeGear.translateBack(0.5,0,0.5);
            timeGear.setChanged();
        }
    }

    @Override
    public void beginFrame(Context ctx) {
        tickGyroscope();
    }
}

