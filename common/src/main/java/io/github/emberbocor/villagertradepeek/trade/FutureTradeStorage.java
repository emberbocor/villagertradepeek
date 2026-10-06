package io.github.emberbocor.villagertradepeek.trade;

import java.util.Optional;

import io.github.emberbocor.villagertradepeek.platform.Services;
import net.minecraft.world.entity.npc.Villager;

public final class FutureTradeStorage {
    private FutureTradeStorage() {
    }

    public static Optional<FutureTrades> get(Villager villager) {
        return Services.PLATFORM.getFutureTrades(villager);
    }

    public static void set(Villager villager, FutureTrades trades) {
        Services.PLATFORM.setFutureTrades(villager, trades);
    }

    public static void remove(Villager villager) {
        Services.PLATFORM.removeFutureTrades(villager);
    }
}
