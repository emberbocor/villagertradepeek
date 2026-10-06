package io.github.emberbocor.villagertradepeek.network;

import io.github.emberbocor.villagertradepeek.mixin.MerchantMenuAccessor;
import io.github.emberbocor.villagertradepeek.platform.Services;
import io.github.emberbocor.villagertradepeek.trade.FutureTradeManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;

public final class FutureTradeSync {
    private static boolean syncing;

    private FutureTradeSync() {
    }

    public static void onMenuOpened(ServerPlayer player, AbstractContainerMenu container) {
        if (container instanceof MerchantMenu menu && ((MerchantMenuAccessor) menu).villagertradepeek$getTrader() instanceof Villager villager) {
            sync(player, menu, villager);
        }
    }

    public static void syncTradingPlayer(Villager villager) {
        if (villager.getTradingPlayer() instanceof ServerPlayer player
                && player.containerMenu instanceof MerchantMenu menu
                && ((MerchantMenuAccessor) menu).villagertradepeek$getTrader() == villager) {
            sync(player, menu, villager);
        }
    }

    private static void sync(ServerPlayer player, MerchantMenu menu, Villager villager) {
        if (syncing) {
            return;
        }
        syncing = true;
        try {
            Services.PLATFORM.sendFutureTrades(player, new FutureTradesPayload(menu.containerId, FutureTradeManager.previewTrades(villager).lockedTrades()));
        } finally {
            syncing = false;
        }
    }
}
