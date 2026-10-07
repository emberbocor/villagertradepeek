package io.github.emberbocor.villagertradepeek.network;

import io.github.emberbocor.villagertradepeek.client.ClientPayloadHandler;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;

public final class ForgeNetwork {
    private static final int PROTOCOL_VERSION = 1;

    private static Channel<CustomPacketPayload> channel;

    private ForgeNetwork() {
    }

    public static void register() {
        channel = ChannelBuilder.named(FutureTradesPayload.TYPE.id())
                .networkProtocolVersion(PROTOCOL_VERSION)
                .payloadChannel()
                .play()
                .clientbound()
                .addMain(FutureTradesPayload.TYPE, FutureTradesPayload.STREAM_CODEC, (payload, context) -> ClientPayloadHandler.handleFutureTrades(payload))
                .build();
    }

    public static boolean isPresent(ServerPlayer player) {
        return channel.isRemotePresent(player.connection.getConnection());
    }

    public static void send(ServerPlayer player, FutureTradesPayload payload) {
        channel.send(payload, PacketDistributor.PLAYER.with(player));
    }

    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FutureTradeSync.onMenuOpened(player, event.getContainer());
        }
    }
}
