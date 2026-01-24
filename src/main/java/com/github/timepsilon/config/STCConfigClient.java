package com.github.timepsilon.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class STCConfigClient {

    public static final STCConfigClient CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.BooleanValue APPLY_SHADER;
    public final ModConfigSpec.BooleanValue SEE_OUT_TRANSLUCENT;

    static {
        Pair<STCConfigClient,ModConfigSpec> pair = new ModConfigSpec.Builder().configure(STCConfigClient::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    private STCConfigClient(ModConfigSpec.Builder builder) {
        APPLY_SHADER = builder
                .comment("Whether or not the out client should get the desaturated shader")
                .translation("config.stonkstimecore.applyShader")
                .define("applyShader", true);
        SEE_OUT_TRANSLUCENT = builder
                .comment("Whether or not to see out players as translucent")
                .translation("config.stonkstimecore.seeOutAsTranslucent")
                .define("seeOutAsTranslucent", true);

    }

}
