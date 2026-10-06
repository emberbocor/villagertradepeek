package io.github.emberbocor.villagertradepeek.trade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.github.emberbocor.villagertradepeek.mixin.AbstractVillagerInvoker;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class FutureTradeGenerator {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int TRADES_PER_LEVEL = 2;

    private FutureTradeGenerator() {
    }

    public static FutureTrades generate(Villager villager, int fromLevel) {
        long startTime = System.nanoTime();
        VillagerProfession profession = villager.getVillagerData().getProfession();
        Int2ObjectMap<VillagerTrades.ItemListing[]> pool = tradePool(villager, profession);
        Map<Integer, List<MerchantOffer>> levels = new HashMap<>();
        if (pool != null) {
            for (int level = fromLevel; level <= VillagerData.MAX_VILLAGER_LEVEL; level++) {
                VillagerTrades.ItemListing[] listings = pool.get(level);
                if (listings != null) {
                    MerchantOffers offers = new MerchantOffers();
                    ((AbstractVillagerInvoker) villager).villagertradepeek$addOffersFromItemListings(offers, DeferredMapTrades.defer(listings), TRADES_PER_LEVEL);
                    levels.put(level, List.copyOf(offers));
                }
            }
        }
        LOGGER.debug("Generated future trades for {} from level {} in {} ms",
                BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession), fromLevel, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
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
