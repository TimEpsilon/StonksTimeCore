package com.github.timepsilon.packets.server;

import com.github.timepsilon.Core;
import com.github.timepsilon.gui.overlay.TimerOverlay;
import com.github.timepsilon.packets.ModPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.PacketListener;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Optional;

public record ApplyShaderPacket(Optional<ResourceLocation> shaderResource) implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, ApplyShaderPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), ApplyShaderPacket::shaderResource,
            ApplyShaderPacket::new
    );

    public ApplyShaderPacket(ResourceLocation location) {
        this(Optional.of(location));
    }

    public static final ApplyShaderPacket EMPTY = new ApplyShaderPacket(Optional.empty());
    public static final ApplyShaderPacket DESATURATE = new ApplyShaderPacket(ResourceLocation.fromNamespaceAndPath(Core.MODID, "shaders/post/desaturate.json"));

    @Override
    public PacketTypeProvider getTypeProvider() {
        return ModPackets.APPLY_SHADER;
    }

    @Override
    public void handle(LocalPlayer player) {
        applyShader(this);
    }

    public static void applyShader(ApplyShaderPacket packet) {
        GameRenderer renderer = Minecraft.getInstance().gameRenderer;
        packet.shaderResource.ifPresentOrElse(
                renderer::loadEffect,
                renderer::shutdownEffect);
    }
}
