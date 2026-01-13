package com.github.timepsilon.utils;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class STCConfig {

    public static final STCConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.IntValue BASE_TIME;
    public final ModConfigSpec.IntValue TIME_TO_MONEY;
    public final ModConfigSpec.IntValue SAFE_TIME;
    public final ModConfigSpec.IntValue DANGER_TIME;
    public final ModConfigSpec.IntValue DT_FOR_GAIN_1HP;
    public final ModConfigSpec.IntValue DT_FOR_LOSE_1HP;
    public final ModConfigSpec.IntValue MAX_HP;
    public final ModConfigSpec.IntValue MIN_HP;

    static {
        Pair<STCConfig,ModConfigSpec> pair = new ModConfigSpec.Builder().configure(STCConfig::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    private STCConfig(ModConfigSpec.Builder builder) {
        builder.translation("config.stonkstimecore.timer").push("timer");
        BASE_TIME = builder
                .comment("The starting time that a new player starts at. (s)")
                .translation("config.stonkstimecore.baseTime")
                .defineInRange("baseTime", 4*60*60, 0, Integer.MAX_VALUE);
        TIME_TO_MONEY = builder
                .comment("Conversion factor to convert the player's time (s) to the player's money.")
                .translation("config.stonkstimecore.timeToMoney")
                .defineInRange("timeToMoney", 2, 1, Integer.MAX_VALUE);
        SAFE_TIME = builder
                .comment("Time above which the player starts gaining max HP. (s)")
                .translation("config.stonkstimecore.safeTime")
                .defineInRange("safeTime", 6*60*60, 0, Integer.MAX_VALUE);
        DANGER_TIME = builder
                .comment("Time under which the player starts losing max HP. (s)")
                .translation("config.stonkstimecore.dangerTime")
                .defineInRange("dangerTime", 30*60, 0, Integer.MAX_VALUE);
        DT_FOR_GAIN_1HP = builder
                .comment("Time needed to get 1 additional max HP once above safeTime. (s)")
                .translation("config.stonkstimecore.dtForGain1HP")
                .defineInRange("dtForGain1HP", 2*60*60, 1, Integer.MAX_VALUE);
        DT_FOR_LOSE_1HP = builder
                .comment("Time needed to lose 1 additional max HP once below dangerTime. (s)")
                .translation("config.stonkstimecore.dtForLose1HP")
                .defineInRange("dtForLose1HP", 3*60, 1, Integer.MAX_VALUE);
        MAX_HP = builder
                .comment("Maximum HP a player can achieve through time.")
                .translation("config.stonkstimecore.maxHP")
                .defineInRange("maxHP",20, 0, 60);
        MIN_HP = builder
                .comment("Minimum HP a player can lose through time.")
                .translation("config.stonkstimecore.minHP")
                .defineInRange("minHP",10, 0, 20);
    }

}
