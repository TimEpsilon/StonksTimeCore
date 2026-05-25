package com.github.timepsilon.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class STCConfigServer {

    public static final STCConfigServer CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.IntValue BASE_TIME;
    public final ModConfigSpec.IntValue TIME_TO_MONEY;
    public final ModConfigSpec.IntValue SAFE_TIME;
    public final ModConfigSpec.IntValue DANGER_TIME;
    public final ModConfigSpec.IntValue DT_FOR_GAIN_1HP;
    public final ModConfigSpec.IntValue DT_FOR_LOSE_1HP;
    public final ModConfigSpec.IntValue MAX_HP;
    public final ModConfigSpec.IntValue MIN_HP;
    public final ModConfigSpec.DoubleValue DEATH_LOSS;
    public final ModConfigSpec.DoubleValue SRE_GAIN_AMOUNT;
    public final ModConfigSpec.DoubleValue SRE_GAIN_ERROR;
    public final ModConfigSpec.DoubleValue SRE_LOSS_AMOUNT;
    public final ModConfigSpec.DoubleValue SRE_LOSS_ERROR;
    public final ModConfigSpec.DoubleValue SRE_TP_DISTANCE;
    public final ModConfigSpec.DoubleValue SRE_TP_PROBABILITY;
    public final ModConfigSpec.IntValue SRE_LIFELINK_DURATION;
    public final ModConfigSpec.IntValue SRE_HOT_POTATO_DURATION;
    public final ModConfigSpec.DoubleValue SRE_SLOW_DOWN_FACTOR;
    public final ModConfigSpec.IntValue SRE_SLOW_DOWN_DURATION;
    public final ModConfigSpec.DoubleValue SRE_SPEED_UP_FACTOR;
    public final ModConfigSpec.IntValue SRE_SPEED_UP_DURATION;
    public final ModConfigSpec.IntValue SRE_LUCKY_SCT_DURATION;
    public final ModConfigSpec.IntValue SRE_TIMELESS_DURATION;
    public final ModConfigSpec.IntValue SRE_TIMELESS_LOSS;
    public final ModConfigSpec.IntValue SRE_GROWTH_SPURT_DURATION;
    public final ModConfigSpec.DoubleValue SRE_GROWTH_SPURT_FACTOR;
    public final ModConfigSpec.IntValue SRE_SHRINKFLATION_DURATION;
    public final ModConfigSpec.DoubleValue SRE_SHRINKFLATION_FACTOR;

    static {
        Pair<STCConfigServer,ModConfigSpec> pair = new ModConfigSpec.Builder().configure(STCConfigServer::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    private STCConfigServer(ModConfigSpec.Builder builder) {
        builder.translation("config.stonkstimecore.timer").push("timer");
        BASE_TIME = builder
                .comment("The starting time that a new player starts at. (s)")
                .translation("config.stonkstimecore.baseTime")
                .defineInRange("baseTime", 4*60*60, 0, Integer.MAX_VALUE);
        TIME_TO_MONEY = builder
                .comment("Conversion factor to convert the player's time (s) to the player's money.")
                .translation("config.stonkstimecore.timeToMoney")
                .defineInRange("timeToMoney", 1, 1, Integer.MAX_VALUE);
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
        DEATH_LOSS = builder
                .comment("Fraction of money that should be lost on player death.")
                .translation("config.stonkstimecore.deathLoss")
                .defineInRange("deathLoss",0.1, 0, 1);

        builder.pop();
        builder.translation("config.stonkstimecore.sre").push("SRE");

        SRE_GAIN_AMOUNT = builder
                .comment("Average amount of time (in seconds) gained for event WinMoney.")
                .translation("config.stonkstimecore.sre.gainAmount")
                .defineInRange("gainAmount", 3600f, 0, Integer.MAX_VALUE);
        SRE_GAIN_ERROR = builder
                .comment("Standard deviation of time (in seconds) gained for event WinMoney.")
                .translation("config.stonkstimecore.sre.gainError")
                .defineInRange("gainError", 600f, 0, Integer.MAX_VALUE);
        SRE_LOSS_AMOUNT = builder
                .comment("Average amount of time (in seconds) lost for event WinMoney.")
                .translation("config.stonkstimecore.sre.lossAmount")
                .defineInRange("lossAmount", 1800f, 0, Integer.MAX_VALUE);
        SRE_LOSS_ERROR = builder
                .comment("Standard deviation of time (in seconds) lost for event WinMoney.")
                .translation("config.stonkstimecore.sre.lossError")
                .defineInRange("lossError", 300f, 0, Integer.MAX_VALUE);
        SRE_TP_DISTANCE = builder
                .comment("Radius in which the player will be teleported.")
                .translation("config.stonkstimecore.sre.tpDistance")
                .defineInRange("tpDistance", 1000f, 0, 10000);
        SRE_TP_PROBABILITY = builder
                .comment("Probability for the player to be teleported in front of a moving train.")
                .translation("config.stonkstimecore.sre.tpProbability")
                .defineInRange("tpProbability", 0.8f, 0, 1);
        SRE_LIFELINK_DURATION = builder
                .comment("Duration (in seconds) for the lifelink effect.")
                .translation("config.stonkstimecore.sre.lifelinkDuration")
                .defineInRange("lifelinkDuration", 3600, 1, Integer.MAX_VALUE);
        SRE_HOT_POTATO_DURATION = builder
                .comment("Duration (in seconds) until the hot potato kills the player.")
                .translation("config.stonkstimecore.sre.hotPotatoDuration")
                .defineInRange("hotPotatoDuration", 600, 1, Integer.MAX_VALUE);
        SRE_SLOW_DOWN_FACTOR = builder
                .comment("Factor by which to slow down time. 1 is 20tps, 0 freezes time completely.")
                .translation("config.stonkstimecore.sre.slowDownFactor")
                .defineInRange("slowDownFactor", 0.25, 0, 1);
        SRE_SLOW_DOWN_DURATION = builder
                .comment("Duration (in seconds, absolute time) during which time will be slowed down.")
                .translation("config.stonkstimecore.sre.slowDownDuration")
                .defineInRange("slowDownDuration", 60, 1, Integer.MAX_VALUE);
        SRE_SPEED_UP_FACTOR = builder
                .comment("Factor by which to speed up time. 1 is 20tps.")
                .translation("config.stonkstimecore.sre.speedUpFactor")
                .defineInRange("speedUpFactor", 4f, 1, 100);
        SRE_SPEED_UP_DURATION = builder
                .comment("Duration (in seconds, absolute time) during which time will be sped up.")
                .translation("config.stonkstimecore.sre.speedUpDuration")
                .defineInRange("speedUpDuration", 60, 1, Integer.MAX_VALUE);
        SRE_LUCKY_SCT_DURATION = builder
                .comment("Duration (in seconds) during which SCT generation is multiplied.")
                .translation("config.stonkstimecore.sre.luckySCTDuration")
                .defineInRange("luckySCTDuration", 900, 1, Integer.MAX_VALUE);
        SRE_TIMELESS_DURATION = builder
                .comment("Duration (in seconds) during which incoming damage drops money.")
                .translation("config.stonkstimecore.sre.timelessDuration")
                .defineInRange("timelessDuration", 600, 1, Integer.MAX_VALUE);
        SRE_TIMELESS_LOSS = builder
                .comment("Amount of money to lose on incoming damage.")
                .translation("config.stonkstimecore.sre.timelessLoss")
                .defineInRange("timelessLoss", 60, 0, Integer.MAX_VALUE);
        SRE_GROWTH_SPURT_DURATION = builder
                .comment("Duration (in seconds) during which the entity will be taller.")
                .translation("config.stonkstimecore.sre.growthSpurtDuration")
                .defineInRange("growthSpurtDuration",120, 1, Integer.MAX_VALUE);
        SRE_GROWTH_SPURT_FACTOR = builder
                .comment("Factor by which to increment the size of the entity, so at 1, a 2m tall player will be 3m at level 1, 4m at level 2, etc.")
                .translation("config.stonkstimecore.sre.growthSpurtFactor")
                .defineInRange("growthSpurtFactor",1f, 0, Integer.MAX_VALUE);
        SRE_SHRINKFLATION_DURATION = builder
                .comment("Duration (in seconds) during which the entity will be shorter.")
                .translation("config.stonkstimecore.sre.shrinkflationtDuration")
                .defineInRange("shrinkflationDuration",120, 1, Integer.MAX_VALUE);
        SRE_SHRINKFLATION_FACTOR = builder
                .comment("Factor by which to divide the size of the entity, so at 2, a 2m tall player will be 1m at level 1, 0.5m at level 2, etc.")
                .translation("config.stonkstimecore.sre.shrinkflationFactor")
                .defineInRange("shrinkflationFactor",2f, 1, Integer.MAX_VALUE);

    }

}
