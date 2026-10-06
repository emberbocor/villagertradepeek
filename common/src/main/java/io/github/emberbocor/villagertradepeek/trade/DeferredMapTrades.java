package io.github.emberbocor.villagertradepeek.trade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.emberbocor.villagertradepeek.VillagerTradePeek;
import io.github.emberbocor.villagertradepeek.mixin.TreasureMapForEmeraldsAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public final class DeferredMapTrades {
    private static final int SEARCH_RADIUS = 100;
    private static final MapCodec<DeferredMap> MARKER_CODEC = DeferredMap.CODEC.fieldOf(VillagerTradePeek.MODID + ":deferred_map");
    private static final Map<ServerLevel, Map<SearchKey, Optional<BlockPos>>> STRUCTURE_CACHE = new WeakHashMap<>();

    private DeferredMapTrades() {
    }

    public static VillagerTrades.ItemListing[] defer(VillagerTrades.ItemListing[] listings) {
        return Arrays.stream(listings)
                .map(listing -> listing.getClass() == VillagerTrades.TreasureMapForEmeralds.class ? new Listing((TreasureMapForEmeraldsAccessor) listing) : listing)
                .toArray(VillagerTrades.ItemListing[]::new);
    }

    public static MerchantOffer resolve(Villager villager, MerchantOffer offer) {
        DeferredMap deferred = deferredMap(offer.getResult());
        if (deferred == null || !(villager.level() instanceof ServerLevel level)) {
            return offer;
        }
        GlobalPos fallback = deferred.fallbackTarget();
        ServerLevel mapLevel = Objects.requireNonNullElse(level.getServer().getLevel(fallback.dimension()), level);
        BlockPos found = mapLevel == level ? level.findNearestMapStructure(deferred.destination(), villager.blockPosition(), SEARCH_RADIUS, true) : null;
        BlockPos target = found != null ? found : fallback.pos();
        ItemStack map = MapItem.create(mapLevel, target.getX(), target.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(mapLevel, map);
        MapItemSavedData.addTargetDecoration(map, target, "+", deferred.decoration());
        map.set(DataComponents.ITEM_NAME, offer.getResult().get(DataComponents.ITEM_NAME));
        return withResult(offer, map);
    }

    public static MerchantOffer forDisplay(MerchantOffer offer) {
        if (deferredMap(offer.getResult()) == null) {
            return offer;
        }
        ItemStack result = offer.getResult().copy();
        result.remove(DataComponents.CUSTOM_DATA);
        return withResult(offer, result);
    }

    private static MerchantOffer withResult(MerchantOffer offer, ItemStack result) {
        return new MerchantOffer(offer.getItemCostA(), offer.getItemCostB(), result, offer.getUses(), offer.getMaxUses(), offer.getXp(), offer.getPriceMultiplier());
    }

    @Nullable
    private static DeferredMap deferredMap(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).read(MARKER_CODEC).result().orElse(null);
    }

    @Nullable
    private static BlockPos locate(ServerLevel level, TagKey<Structure> destination, BlockPos origin) {
        return STRUCTURE_CACHE.computeIfAbsent(level, key -> new HashMap<>())
                .computeIfAbsent(new SearchKey(destination, new ChunkPos(origin)),
                        key -> Optional.ofNullable(level.findNearestMapStructure(destination, origin, SEARCH_RADIUS, false)))
                .orElse(null);
    }

    private record SearchKey(TagKey<Structure> destination, ChunkPos chunk) {
    }

    private record DeferredMap(TagKey<Structure> destination, Holder<MapDecorationType> decoration, GlobalPos fallbackTarget) {
        private static final Codec<DeferredMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TagKey.codec(Registries.STRUCTURE).fieldOf("destination").forGetter(DeferredMap::destination),
                MapDecorationType.CODEC.fieldOf("decoration").forGetter(DeferredMap::decoration),
                GlobalPos.CODEC.fieldOf("fallback_target").forGetter(DeferredMap::fallbackTarget)
        ).apply(instance, DeferredMap::new));
    }

    private record Listing(TreasureMapForEmeraldsAccessor map) implements VillagerTrades.ItemListing {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource random) {
            if (!(trader.level() instanceof ServerLevel level)) {
                return null;
            }
            TagKey<Structure> destination = map.villagertradepeek$getDestination();
            BlockPos target = locate(level, destination, trader.blockPosition());
            if (target == null) {
                return null;
            }
            ItemStack placeholder = new ItemStack(Items.FILLED_MAP);
            placeholder.set(DataComponents.ITEM_NAME, Component.translatable(map.villagertradepeek$getDisplayName()));
            placeholder.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY
                    .update(NbtOps.INSTANCE, MARKER_CODEC, new DeferredMap(destination, map.villagertradepeek$getDestinationType(), GlobalPos.of(level.dimension(), target)))
                    .getOrThrow());
            return new MerchantOffer(new ItemCost(Items.EMERALD, map.villagertradepeek$getEmeraldCost()), Optional.of(new ItemCost(Items.COMPASS)),
                    placeholder, map.villagertradepeek$getMaxUses(), map.villagertradepeek$getVillagerXp(), 0.2F);
        }
    }
}
