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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    private void villagertradepeek$saveFutureTrades(ValueOutput output, CallbackInfo ci) {
        FutureTradeStorage.save(output, villagertradepeek$futureTrades);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void villagertradepeek$loadFutureTrades(ValueInput input, CallbackInfo ci) {
        villagertradepeek$futureTrades = FutureTradeStorage.load(input, ((Villager) (Object) this).registryAccess());
    }

    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    private void villagertradepeek$unlockStoredTrades(ServerLevel level, CallbackInfo ci) {
        if (FutureTradeManager.unlockStoredTrades(level, (Villager) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "updateTrades", at = @At("TAIL"))
    private void villagertradepeek$discardRerolledFutureTrades(ServerLevel level, CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;
        if (FutureTradeManager.discardOnLevelOneTrades(villager)) {
            FutureTradeSync.syncTradingPlayer(villager);
        }
    }
}
