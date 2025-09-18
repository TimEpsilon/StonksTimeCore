package com.github.timepsilon.block.entity.client;

import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class StonksTemporalChronoscopeRenderer extends GeoBlockRenderer<StonksTemporalChronoscopeEntity> {

    public StonksTemporalChronoscopeRenderer(BlockEntityRendererProvider.Context context) {
        super(new StonksTemporalChronoscopeModel());
    }
}
