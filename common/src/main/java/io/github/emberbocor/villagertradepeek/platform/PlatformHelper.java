package io.github.emberbocor.villagertradepeek.platform;

import java.util.Optional;

import io.github.emberbocor.villagertradepeek.network.FutureTradesPayload;
import io.github.emberbocor.villagertradepeek.trade.FutureTrades;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;

public interface PlatformHelper {
    void sendFutureTrades(ServerPlayer player, FutureTradesPayload payload);

    Optional<FutureTrades> getFutureTrades(Villager villager);

    void setFutureTrades(Villager villager, FutureTrades trades);

    void removeFutureTrades(Villager villager);
}
