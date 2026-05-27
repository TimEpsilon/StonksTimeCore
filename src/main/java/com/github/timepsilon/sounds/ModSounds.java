package com.github.timepsilon.sounds;

import com.github.timepsilon.Core;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Core.MODID);

    public static Supplier<SoundEvent> TIME_OUT = registerSoundEvent("time_out");
    public static Supplier<SoundEvent> SLOT_MACHINE_SPINNING = registerSoundEvent("slot_machine.spinning");
    public static Supplier<SoundEvent> SLOT_MACHINE_PULLING = registerSoundEvent("slot_machine.pulling");
    public static Supplier<SoundEvent> SLOT_MACHINE_WINNING = registerSoundEvent("slot_machine.winning");
    public static Supplier<SoundEvent> SLOT_MACHINE_LOSING = registerSoundEvent("slot_machine.losing");


    private static Supplier<SoundEvent> registerSoundEvent(String id) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(Core.MODID, id);
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(resourceLocation));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

}
