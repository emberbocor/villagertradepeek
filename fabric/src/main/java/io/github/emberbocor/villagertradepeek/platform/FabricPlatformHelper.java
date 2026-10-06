package io.github.emberbocor.villagertradepeek.platform;

import io.github.emberbocor.villagertradepeek.network.FutureTradesPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

public final class FabricPlatformHelper implements PlatformHelper {
    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean canReceiveFutureTrades(ServerPlayer player) {
        return ServerPlayNetworking.canSend(player, FutureTradesPayload.TYPE);
    }

    @Override
    public void sendFutureTrades(ServerPlayer player, FutureTradesPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}
