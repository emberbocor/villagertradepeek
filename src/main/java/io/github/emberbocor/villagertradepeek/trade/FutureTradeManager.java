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
        FutureTrades stored = villager.getExistingDataOrNull(ModAttachments.FUTURE_TRADES);
        if (stored == null) {
            return false;
        }
        VillagerData data = villager.getVillagerData();
        if (stored.profession() != data.getProfession()) {
            villager.removeData(ModAttachments.FUTURE_TRADES);
            return false;
        }
        List<MerchantOffer> offers = stored.levels().get(data.getLevel());
        if (offers == null) {
            return false;
        }
        villager.getOffers().addAll(offers);
        store(villager, stored.withoutLevel(data.getLevel()));
        return true;
    }

    @Nullable
    public static FutureTrades currentTrades(Villager villager) {
        FutureTrades stored = villager.getExistingDataOrNull(ModAttachments.FUTURE_TRADES);
        return stored != null && stored.profession() == villager.getVillagerData().getProfession() ? stored : null;
    }

    public static void onTradesUpdated(Villager villager) {
        if (villager.getVillagerData().getLevel() == VillagerData.MIN_VILLAGER_LEVEL) {
            store(villager, FutureTradeGenerator.generate(villager, VillagerData.MIN_VILLAGER_LEVEL + 1));
        }
    }

    private static void store(Villager villager, FutureTrades trades) {
        if (trades.isEmpty()) {
            villager.removeData(ModAttachments.FUTURE_TRADES);
        } else {
            villager.setData(ModAttachments.FUTURE_TRADES, trades);
        }
    }
}
