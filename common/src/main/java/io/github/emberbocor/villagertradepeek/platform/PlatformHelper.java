package io.github.emberbocor.villagertradepeek.platform;

import io.github.emberbocor.villagertradepeek.network.FutureTradesPayload;
import net.minecraft.server.level.ServerPlayer;

public interface PlatformHelper {
    boolean isDevelopmentEnvironment();

    void sendFutureTrades(ServerPlayer player, FutureTradesPayload payload);
}
