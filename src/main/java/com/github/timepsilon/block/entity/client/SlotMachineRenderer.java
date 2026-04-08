package com.github.timepsilon.block.entity.client;

import com.github.timepsilon.block.entity.server.SlotMachineEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SlotMachineRenderer extends GeoBlockRenderer<SlotMachineEntity> {

    public SlotMachineRenderer(BlockEntityRendererProvider.Context context) {
        super(new SlotMachineModel());
    }

    @Override
    public @Nullable RenderType getRenderType(SlotMachineEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
