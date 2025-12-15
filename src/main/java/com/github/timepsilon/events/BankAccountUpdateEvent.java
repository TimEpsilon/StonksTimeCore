package com.github.timepsilon.events;

import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.neoforged.bus.api.Event;

public class BankAccountUpdateEvent extends Event {

    private BankAccount bank;
    private double value;

    public BankAccountUpdateEvent(BankAccount bank, double value) {
        this.bank = bank;
        this.value = value;
    }

    public BankAccount getBank() {
        return bank;
    }

    public double getValue() {
        return value;
    }

}
