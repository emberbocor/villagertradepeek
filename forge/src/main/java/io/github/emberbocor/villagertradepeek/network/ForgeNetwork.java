package io.github.emberbocor.villagertradepeek.network;

import io.github.emberbocor.villagertradepeek.client.ClientPayloadHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ForgeNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(FutureTradesPayload.ID,
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private ForgeNetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(FutureTradesPayload.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(FutureTradesPayload::write)
                .decoder(FutureTradesPayload::read)
                .consumerMainThread((payload, context) -> ClientPayloadHandler.handleFutureTrades(payload))
                .add();
    }

    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FutureTradeSync.onMenuOpened(player, event.getContainer());
        }
    }
}
