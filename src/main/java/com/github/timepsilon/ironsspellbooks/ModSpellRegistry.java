package com.github.timepsilon.ironsspellbooks;

import com.github.timepsilon.Core;
import com.github.timepsilon.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.core.Holder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSpellRegistry {

    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, Core.MODID);

    public static final Holder<AbstractSpell> LOSE_MONEY = registerSpell(new LoseMoney());
    public static final Holder<AbstractSpell> SLOW_DOWN = registerSpell(new SlowDown());
    public static final Holder<AbstractSpell> SPEED_UP = registerSpell(new SpeedUp());
    public static final Holder<AbstractSpell> BOOST = registerSpell(new Boost());
    public static final Holder<AbstractSpell> MIRROR = registerSpell(new Mirror());
    public static final Holder<AbstractSpell> GROWTH_SPURT = registerSpell(new GrowthSpurt());
    public static final Holder<AbstractSpell> SHRINKFLATION = registerSpell(new Shrinkflation());
    public static final Holder<AbstractSpell> TIMELESS = registerSpell(new Timeless());
    public static final Holder<AbstractSpell> LIFELINK = registerSpell(new Lifelink());
    public static final Holder<AbstractSpell> HOT_POTATO = registerSpell(new HotPotato());

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }

    public static DeferredHolder<AbstractSpell, AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

}
