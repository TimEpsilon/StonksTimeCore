package com.github.timepsilon.ironsspellbooks;

import com.github.timepsilon.Core;
import com.github.timepsilon.ironsspellbooks.spells.losemoney.LoseMoney;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSpellRegistry {

    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, Core.MODID);

    public static final DeferredHolder<AbstractSpell, AbstractSpell> LOSE_MONEY = registerSpell(new LoseMoney());

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }

    public static DeferredHolder<AbstractSpell, AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

}
