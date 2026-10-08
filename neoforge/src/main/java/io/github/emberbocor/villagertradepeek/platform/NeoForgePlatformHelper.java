package io.github.emberbocor.villagertradepeek.platform;

import io.github.emberbocor.villagertradepeek.network.FutureTradesPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;

public final class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.isProduction();
    }

    @Override
    public boolean canReceiveFutureTrades(ServerPlayer player) {
        return player.connection.hasChannel(FutureTradesPayload.TYPE);
    }

    @Override
    public void sendFutureTrades(ServerPlayer player, FutureTradesPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
