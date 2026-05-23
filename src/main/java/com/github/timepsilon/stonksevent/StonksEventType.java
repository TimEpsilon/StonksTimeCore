package com.github.timepsilon.stonksevent;

import com.github.timepsilon.stonksevent.boost.SREBoost;
import com.github.timepsilon.stonksevent.giveitem.SREGiveItem;
import com.github.timepsilon.stonksevent.hotpotato.SREHotPotato;
import com.github.timepsilon.stonksevent.lifelink.SRELifeLink;
import com.github.timepsilon.stonksevent.losemoney.SRELoseMoney;
import com.github.timepsilon.stonksevent.luckysct.SRELuckySCT;
import com.github.timepsilon.stonksevent.oopsallones.SREOopsAllOnes;
import com.github.timepsilon.stonksevent.slowdown.SRESlowDown;
import com.github.timepsilon.stonksevent.spawnmob.SRESpawnMob;
import com.github.timepsilon.stonksevent.speedup.SRESpeedUp;
import com.github.timepsilon.stonksevent.teleport.SRETeleport;
import com.github.timepsilon.stonksevent.winmoney.SREWinMoney;
import com.github.timepsilon.utils.Scheduler;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum StonksEventType implements StringRepresentable {

    WIN_MONEY(new SREWinMoney(5, true, "CCC", "win_money")),
    GIVE_ITEM(new SREGiveItem(5, true, "777",  "give_item")),
    LUCKY_SCT(new SRELuckySCT(5, true, "MMM", "lucky_sct")),
    SLOW_DOWN(new SRESlowDown(5, false, "0MC", "slow_down")),
    SPEED_UP(new SRESpeedUp(5, false, "0M7", "speed_up")),
    LIFELINK(new SRELifeLink(5, false, "M77", "lifelink")),
    SPAWN_MOB(new SRESpawnMob(5, false, "7CM", "spawn_mob")),
    LOSE_MONEY(new SRELoseMoney(5, false, "0CC", "lose_money")),
    TELEPORT(new SRETeleport(5, false, "MM0", "teleport")),
    HOT_POTATO(new SREHotPotato(5, false, "CM7",  "hot_potato")),
    OOPS_ALL_ONES(new SREOopsAllOnes(5, false, "7MM", "oops_all_ones")),
    BOOST(new SREBoost(5, false, "C0C", "boost")),

    ;

    private final AbstractRandomStonksEvent instance;

    StonksEventType(AbstractRandomStonksEvent instance) {
        this.instance = instance;
    }

    public static StonksEventType startRandomEvent(Player player, int delay) {
        float totalWeight = 0;

        for (StonksEventType eventType : StonksEventType.values()) {
            totalWeight += eventType.getWeight();
        }

        float sample = ThreadLocalRandom.current().nextFloat() * totalWeight;
        float x = 0;

        for  (StonksEventType eventType : StonksEventType.values()) {
            x += eventType.getWeight();

            if (x >= sample) {
                return startGivenEvent(player, eventType, delay);
            }
        }
        return null;
    }

    public static StonksEventType startGivenEvent(Player player, StonksEventType eventType) {
        return startGivenEvent(player, eventType, 0);
    }

    public static StonksEventType startGivenEvent(Player player, StonksEventType eventType, int delay) {
        float s = player.getServer().tickRateManager().millisecondsPerTick()/1000;
        Scheduler.runLater((int)(delay/s), () -> eventType.instance.start(player));
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
