package io.github.emberbocor.villagertradepeek.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.villagertradepeek.network.FutureTradeSync;
import io.github.emberbocor.villagertradepeek.trade.FutureTradeManager;
import io.github.emberbocor.villagertradepeek.trade.FutureTradeStorage;
import io.github.emberbocor.villagertradepeek.trade.FutureTrades;
import io.github.emberbocor.villagertradepeek.trade.FutureTradesHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.Villager;

@Mixin(Villager.class)
public abstract class VillagerMixin implements FutureTradesHolder {
    @Unique
    @Nullable
    private FutureTrades villagertradepeek$futureTrades;

    @Override
    @Nullable
    public FutureTrades villagertradepeek$getFutureTrades() {
        return villagertradepeek$futureTrades;
    }

    @Override
    public void villagertradepeek$setFutureTrades(@Nullable FutureTrades trades) {
        villagertradepeek$futureTrades = trades;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void villagertradepeek$saveFutureTrades(CompoundTag tag, CallbackInfo ci) {
        FutureTradeStorage.save((Villager) (Object) this, villagertradepeek$futureTrades, tag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void villagertradepeek$loadFutureTrades(CompoundTag tag, CallbackInfo ci) {
        FutureTradeStorage.load((Villager) (Object) this, tag);
    }

    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    private void villagertradepeek$unlockStoredTrades(CallbackInfo ci) {
        if (FutureTradeManager.unlockStoredTrades((Villager) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "updateTrades", at = @At("TAIL"))
    private void villagertradepeek$discardRerolledFutureTrades(CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;
        if (FutureTradeManager.discardOnLevelOneTrades(villager)) {
            FutureTradeSync.syncTradingPlayer(villager);
        }
    }
}
