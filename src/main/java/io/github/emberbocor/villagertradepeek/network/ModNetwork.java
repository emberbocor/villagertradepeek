package io.github.emberbocor.villagertradepeek.network;

import io.github.emberbocor.villagertradepeek.client.ClientPayloadHandler;
import io.github.emberbocor.villagertradepeek.mixin.MerchantMenuAccessor;
import io.github.emberbocor.villagertradepeek.trade.FutureTradeManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.MerchantMenu;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private static boolean syncing;

    private ModNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION).playToClient(FutureTradesPayload.TYPE, FutureTradesPayload.STREAM_CODEC,
                (payload, context) -> ClientPayloadHandler.handleFutureTrades(payload));
    }

    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer player
                && event.getContainer() instanceof MerchantMenu menu
                && ((MerchantMenuAccessor) menu).villagertradepeek$getTrader() instanceof Villager villager) {
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
            PacketDistributor.sendToPlayer(player, new FutureTradesPayload(menu.containerId, FutureTradeManager.previewTrades(villager).lockedTrades()));
        } finally {
            syncing = false;
        }
    }
}
