package io.github.emberbocor.villagertradepeek.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.villagertradepeek.trade.FutureTradeManager;
import net.minecraft.world.entity.npc.Villager;

@Mixin(Villager.class)
public abstract class VillagerMixin {
    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    private void villagertradepeek$unlockStoredTrades(CallbackInfo ci) {
        if (FutureTradeManager.unlockStoredTrades((Villager) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "updateTrades", at = @At("TAIL"))
    private void villagertradepeek$discardRerolledFutureTrades(CallbackInfo ci) {
        FutureTradeManager.onTradesUpdated((Villager) (Object) this);
    }
}
