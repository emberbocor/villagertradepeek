package io.github.emberbocor.villagertradepeek.trade;

import java.util.List;

import javax.annotation.Nullable;

import io.github.emberbocor.villagertradepeek.ModAttachments;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.item.trading.MerchantOffer;

public final class FutureTradeManager {
    private FutureTradeManager() {
    }

    public static boolean unlockStoredTrades(Villager villager) {
        FutureTrades stored = storedTrades(villager);
        int level = villager.getVillagerData().getLevel();
        List<MerchantOffer> offers = stored != null ? stored.levels().get(level) : null;
        if (offers == null) {
            return false;
        }
        for (MerchantOffer offer : offers) {
            villager.getOffers().add(DeferredMapTrades.resolve(villager, offer));
        }
        store(villager, stored.above(level));
        return true;
    }

    public static FutureTrades previewTrades(Villager villager) {
        villager.getOffers();
        int level = villager.getVillagerData().getLevel();
        FutureTrades stored = storedTrades(villager);
        FutureTrades trades = stored != null ? stored.above(level) : FutureTradeGenerator.generate(villager, level + 1);
        if (!trades.equals(stored)) {
            store(villager, trades);
        }
        return trades;
    }

    public static boolean discardOnLevelOneTrades(Villager villager) {
        if (villager.getVillagerData().getLevel() != VillagerData.MIN_VILLAGER_LEVEL) {
            return false;
        }
        villager.removeData(ModAttachments.FUTURE_TRADES);
        return true;
    }

    @Nullable
    private static FutureTrades storedTrades(Villager villager) {
        FutureTrades stored = villager.getExistingData(ModAttachments.FUTURE_TRADES).orElse(null);
        return stored != null && stored.profession() == villager.getVillagerData().getProfession() ? stored : null;
    }

    private static void store(Villager villager, FutureTrades trades) {
        if (trades.levels().isEmpty()) {
            villager.removeData(ModAttachments.FUTURE_TRADES);
        } else {
            villager.setData(ModAttachments.FUTURE_TRADES, trades);
        }
    }
}
