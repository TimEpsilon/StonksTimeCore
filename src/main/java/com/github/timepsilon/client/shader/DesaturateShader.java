package com.github.timepsilon.client.shader;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;

public class DesaturateShader {

    public static void test(EffectInstance effectInstance) {
        Uniform uniform = effectInstance.getUniform("test");
    }
}
