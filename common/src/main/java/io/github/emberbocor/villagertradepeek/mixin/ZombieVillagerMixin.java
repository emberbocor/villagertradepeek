package io.github.emberbocor.villagertradepeek.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.villagertradepeek.trade.FutureTradeStorage;
import io.github.emberbocor.villagertradepeek.trade.FutureTrades;
import io.github.emberbocor.villagertradepeek.trade.FutureTradesHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.ZombieVillager;

@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerMixin implements FutureTradesHolder {
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
        FutureTradeStorage.save((ZombieVillager) (Object) this, villagertradepeek$futureTrades, tag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void villagertradepeek$loadFutureTrades(CompoundTag tag, CallbackInfo ci) {
        FutureTradeStorage.load((ZombieVillager) (Object) this, tag);
    }
}
