package io.github.emberbocor.villagertradepeek.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.villagertradepeek.network.ModNetwork;
import io.github.emberbocor.villagertradepeek.trade.FutureTradeManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

@Mixin(Villager.class)
public abstract class VillagerMixin {
    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    private void villagertradepeek$unlockStoredTrades(CallbackInfo ci) {
        if (FutureTradeManager.unlockStoredTrades((Villager) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "updateTrades", at = @At("TAIL"))
    private void villagertradepeek$generateFutureTrades(CallbackInfo ci) {
        FutureTradeManager.onTradesUpdated((Villager) (Object) this);
    }

    @Inject(method = "startTrading", at = @At("TAIL"))
    private void villagertradepeek$sendFutureTrades(Player player, CallbackInfo ci) {
        if (player instanceof ServerPlayer serverPlayer) {
            ModNetwork.sendFutureTrades(serverPlayer, (Villager) (Object) this);
        }
    }
}
