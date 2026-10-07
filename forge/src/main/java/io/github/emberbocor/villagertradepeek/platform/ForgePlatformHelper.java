package io.github.emberbocor.villagertradepeek.platform;

import io.github.emberbocor.villagertradepeek.network.ForgeNetwork;
import io.github.emberbocor.villagertradepeek.network.FutureTradesPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class ForgePlatformHelper implements PlatformHelper {
    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    @Override
    public boolean canReceiveFutureTrades(ServerPlayer player) {
        return ForgeNetwork.isPresent(player);
    }

    @Override
    public void sendFutureTrades(ServerPlayer player, FutureTradesPayload payload) {
        ForgeNetwork.send(player, payload);
    }
}
