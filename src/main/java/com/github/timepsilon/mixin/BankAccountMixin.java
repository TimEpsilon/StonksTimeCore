package com.github.timepsilon.mixin;

import com.github.timepsilon.events.BankAccountUpdateEvent;
import dev.ithundxr.createnumismatics.content.backend.BankAccount;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BankAccount.class)
public class BankAccountMixin {

    @Inject(method="setBalance", at=@At("TAIL"))
    private void onSetBalance(int balance, CallbackInfo ci) {
        BankAccount bank = (BankAccount)(Object)this;

        NeoForge.EVENT_BUS.post(
                new BankAccountUpdateEvent(bank, balance)
        );
    }
}
