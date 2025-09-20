package com.github.timepsilon.block.entity.client;

import com.github.timepsilon.block.entity.server.StonksTemporalChronoscopeEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class StonksTemporalChronoscopeRenderer extends GeoBlockRenderer<StonksTemporalChronoscopeEntity> {

    public StonksTemporalChronoscopeRenderer(BlockEntityRendererProvider.Context context) {
        super(new StonksTemporalChronoscopeModel());
        AutoGlowingTexture.PRINT_DEBUG_IMAGES = true;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
