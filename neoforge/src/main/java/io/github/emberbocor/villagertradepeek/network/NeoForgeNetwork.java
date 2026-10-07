package io.github.emberbocor.villagertradepeek.network;

import io.github.emberbocor.villagertradepeek.client.ClientPayloadHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NeoForgeNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private NeoForgeNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION).playToClient(FutureTradesPayload.TYPE, FutureTradesPayload.STREAM_CODEC,
                (payload, context) -> ClientPayloadHandler.handleFutureTrades(payload));
    }

    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FutureTradeSync.onMenuOpened(player, event.getContainer());
        }
    }
}
