package io.github.emberbocor.villagertradepeek.trade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.github.emberbocor.villagertradepeek.mixin.AbstractVillagerInvoker;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class FutureTradeGenerator {
    private static final int TRADES_PER_LEVEL = 2;

    private FutureTradeGenerator() {
    }

    public static FutureTrades generate(Villager villager, int fromLevel) {
        VillagerProfession profession = villager.getVillagerData().getProfession();
        Int2ObjectMap<VillagerTrades.ItemListing[]> pool = tradePool(villager, profession);
        Map<Integer, List<MerchantOffer>> levels = new HashMap<>();
        if (pool != null) {
            for (int level = fromLevel; level <= VillagerData.MAX_VILLAGER_LEVEL; level++) {
                VillagerTrades.ItemListing[] listings = pool.get(level);
                if (listings != null) {
                    MerchantOffers offers = new MerchantOffers();
                    ((AbstractVillagerInvoker) villager).villagertradepeek$addOffersFromItemListings(offers, listings, TRADES_PER_LEVEL);
                    levels.put(level, List.copyOf(offers));
                }
            }
        }
        return new FutureTrades(profession, levels);
    }

    @Nullable
    private static Int2ObjectMap<VillagerTrades.ItemListing[]> tradePool(Villager villager, VillagerProfession profession) {
        if (villager.level().enabledFeatures().contains(FeatureFlags.TRADE_REBALANCE)) {
            Int2ObjectMap<VillagerTrades.ItemListing[]> experimental = VillagerTrades.EXPERIMENTAL_TRADES.get(profession);
            if (experimental != null) {
                return experimental;
            }
        }
        return VillagerTrades.TRADES.get(profession);
    }
}
