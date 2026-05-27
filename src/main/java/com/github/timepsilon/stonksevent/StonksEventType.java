package com.github.timepsilon.stonksevent;

import com.github.timepsilon.block.entity.server.SlotMachineEntity;
import com.github.timepsilon.stonksevent.boost.SREBoost;
import com.github.timepsilon.stonksevent.giveitem.SREGiveItem;
import com.github.timepsilon.stonksevent.growthspurt.SREGrowthSpurt;
import com.github.timepsilon.stonksevent.hotpotato.SREHotPotato;
import com.github.timepsilon.stonksevent.lifelink.SRELifeLink;
import com.github.timepsilon.stonksevent.losemoney.SRELoseMoney;
import com.github.timepsilon.stonksevent.luckysct.SRELuckySCT;
import com.github.timepsilon.stonksevent.mirror.SREMirror;
import com.github.timepsilon.stonksevent.oopsallones.SREOopsAllOnes;
import com.github.timepsilon.stonksevent.shrinkflation.SREShrinkflation;
import com.github.timepsilon.stonksevent.slowdown.SRESlowDown;
import com.github.timepsilon.stonksevent.spawnmob.SRESpawnMob;
import com.github.timepsilon.stonksevent.speedup.SRESpeedUp;
import com.github.timepsilon.stonksevent.teleport.SRETeleport;
import com.github.timepsilon.stonksevent.timeless.SRETimeless;
import com.github.timepsilon.stonksevent.winmoney.SREWinMoney;
import com.github.timepsilon.utils.Scheduler;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum StonksEventType implements StringRepresentable {

    // Positive events
    WIN_MONEY(new SREWinMoney(12, true, "CCC", "win_money")),
    GIVE_ITEM(new SREGiveItem(20, true, "777",  "give_item")),
    LUCKY_SCT(new SRELuckySCT(5, true, "MMM", "lucky_sct")),

    // Negative events
    SLOW_DOWN(new SRESlowDown(4, false, "0MC", "slow_down")),
    SPEED_UP(new SRESpeedUp(4, false, "0M7", "speed_up")),
    LIFELINK(new SRELifeLink(4, false, "M77", "lifelink")),
    SPAWN_MOB(new SRESpawnMob(4, false, "7CM", "spawn_mob")),
    LOSE_MONEY(new SRELoseMoney(3, false, "0CC", "lose_money")),
    TELEPORT(new SRETeleport(3, false, "MM0", "teleport")),
    HOT_POTATO(new SREHotPotato(3, false, "000",  "hot_potato")),
    TIMELESS(new SRETimeless(3, false, "077", "timeless")),
    BOOST(new SREBoost(2, false, "C0C", "boost")),
    GROWTH_SPURT(new SREGrowthSpurt(2, false, "7CC", "growth_spurt")),
    SHRINKFLATION(new SREShrinkflation(2, false, "MCC", "shrinkflation")),
    MIRROR(new SREMirror(2, false, "007", "mirror")),
    OOPS_ALL_ONES(new SREOopsAllOnes(1, false, "7MM", "oops_all_ones")),
    ;

    private final AbstractRandomStonksEvent instance;

    StonksEventType(AbstractRandomStonksEvent instance) {
        this.instance = instance;
    }

    public static StonksEventType startRandomEvent(Player player, float delay, @Nullable SlotMachineEntity slotMachineEntity) {
        float totalWeight = 0;

        for (StonksEventType eventType : StonksEventType.values()) {
            totalWeight += eventType.getWeight();
        }

        float sample = ThreadLocalRandom.current().nextFloat() * totalWeight;
        float x = 0;

        for (StonksEventType eventType : StonksEventType.values()) {
            x += eventType.getWeight();

            if (x >= sample) {
                return startGivenEvent(player, eventType, delay, slotMachineEntity);
            }
        }
        return null;
    }

    public static StonksEventType startGivenEvent(Player player, StonksEventType eventType) {
        return startGivenEvent(player, eventType, 0, null);
    }

    public static StonksEventType startGivenEvent(Player player, StonksEventType eventType, float delay, @Nullable SlotMachineEntity slotMachineEntity) {
        float s = player.getServer().tickRateManager().millisecondsPerTick()/1000;
        Scheduler.runLater((int)(delay/s), () -> {
            eventType.getEvent().start(player);
            if (slotMachineEntity != null) slotMachineEntity.score(eventType.getEvent().isPositive());
        });
        return eventType;
    }

    public static StonksEventType stopGivenEvent(Player player, StonksEventType eventType) {
        eventType.instance.stop(player);
        return eventType;
    }

    public float getWeight() {
        return this.instance.getWeight();
    }

    public List<AbstractRandomStonksEvent.Symbol> getCombination() {
        return this.instance.getCombination();
    }

    public AbstractRandomStonksEvent getEvent() {
        return this.instance;
    }


    @Override
    public String getSerializedName() {
        return instance.getName();
    }
}
