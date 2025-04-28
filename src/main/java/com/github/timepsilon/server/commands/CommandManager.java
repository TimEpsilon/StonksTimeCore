package com.github.timepsilon.server.commands;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;


public class CommandManager {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {

        GenerateEquivalency.register(event.getDispatcher());
    }
}
