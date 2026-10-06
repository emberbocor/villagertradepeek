package io.github.emberbocor.villagertradepeek.platform;

import java.util.Optional;

import io.github.emberbocor.villagertradepeek.ModAttachments;
import io.github.emberbocor.villagertradepeek.network.FutureTradesPayload;
import io.github.emberbocor.villagertradepeek.trade.FutureTrades;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.neoforge.network.PacketDistributor;

public final class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public void sendFutureTrades(ServerPlayer player, FutureTradesPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public Optional<FutureTrades> getFutureTrades(Villager villager) {
        return villager.getExistingData(ModAttachments.FUTURE_TRADES);
    }

    @Override
    public void setFutureTrades(Villager villager, FutureTrades trades) {
        villager.setData(ModAttachments.FUTURE_TRADES, trades);
    }

    @Override
    public void removeFutureTrades(Villager villager) {
        villager.removeData(ModAttachments.FUTURE_TRADES);
    }
}
