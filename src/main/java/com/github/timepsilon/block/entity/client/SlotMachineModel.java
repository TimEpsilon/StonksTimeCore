package com.github.timepsilon.block.entity.client;

import com.github.timepsilon.Core;
import com.github.timepsilon.block.entity.server.SlotMachineEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SlotMachineModel extends GeoModel<SlotMachineEntity> {

    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Core.MODID, "geo/slot_machine/slot_machine.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Core.MODID, "textures/block/slot_machine.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Core.MODID, "animations/slot_machine/slot_machine.animation.json");

    @Override
    public ResourceLocation getModelResource(SlotMachineEntity slotMachineEntity) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(SlotMachineEntity slotMachineEntity) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SlotMachineEntity slotMachineEntity) {
        return animations;
    }
}
