package io.github.emberbocor.villagertradepeek.trade;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.item.trading.MerchantOffer;

public final class FutureTradeManager {
    private FutureTradeManager() {
    }

    public static boolean unlockStoredTrades(ServerLevel serverLevel, Villager villager) {
        FutureTrades stored = storedTrades(villager);
        int level = villager.getVillagerData().level();
        List<MerchantOffer> offers = stored != null ? stored.levels().get(level) : null;
        if (offers == null) {
            return false;
        }
        for (MerchantOffer offer : offers) {
            villager.getOffers().add(DeferredMapTrades.resolve(serverLevel, villager, offer));
        }
        store(villager, stored.above(level));
        return true;
    }

    public static FutureTrades previewTrades(ServerLevel serverLevel, Villager villager) {
        villager.getOffers();
        int level = villager.getVillagerData().level();
        FutureTrades stored = storedTrades(villager);
        FutureTrades trades = stored != null ? stored.above(level) : FutureTradeGenerator.generate(serverLevel, villager, level + 1);
        if (!trades.equals(stored)) {
            store(villager, trades);
        }
        return trades;
    }

    public static boolean discardOnLevelOneTrades(Villager villager) {
        if (villager.getVillagerData().level() != VillagerData.MIN_VILLAGER_LEVEL) {
            return false;
        }
        FutureTradeStorage.remove(villager);
        return true;
    }

    @Nullable
    private static FutureTrades storedTrades(Villager villager) {
        FutureTrades stored = FutureTradeStorage.get(villager).orElse(null);
        return stored != null && stored.profession().equals(villager.getVillagerData().profession()) ? stored : null;
    }

    private static void store(Villager villager, FutureTrades trades) {
        if (trades.levels().isEmpty()) {
            FutureTradeStorage.remove(villager);
        } else {
            FutureTradeStorage.set(villager, trades);
        }
    }
}
