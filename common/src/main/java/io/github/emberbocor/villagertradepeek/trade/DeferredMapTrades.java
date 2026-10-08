package io.github.emberbocor.villagertradepeek.trade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.WeakHashMap;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.emberbocor.villagertradepeek.VillagerTradePeek;
import io.github.emberbocor.villagertradepeek.mixin.TreasureMapForEmeraldsAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public final class DeferredMapTrades {
    private static final int SEARCH_RADIUS = 100;
    private static final String MARKER_KEY = VillagerTradePeek.MODID + ":deferred_map";
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
        if (deferred == null || !(villager.level instanceof ServerLevel level)) {
            return offer;
        }
        GlobalPos fallback = deferred.fallbackTarget();
        ServerLevel mapLevel = Objects.requireNonNullElse(level.getServer().getLevel(fallback.dimension()), level);
        BlockPos found = mapLevel == level ? level.findNearestMapFeature(deferred.destination(), villager.blockPosition(), SEARCH_RADIUS, true) : null;
        BlockPos target = found != null ? found : fallback.pos();
        ItemStack map = MapItem.create(mapLevel, target.getX(), target.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(mapLevel, map);
        MapItemSavedData.addTargetDecoration(map, target, "+", deferred.decoration());
        map.setHoverName(offer.getResult().getHoverName());
        return withResult(offer, map);
    }

    public static MerchantOffer forDisplay(MerchantOffer offer) {
        if (deferredMap(offer.getResult()) == null) {
            return offer;
        }
        ItemStack result = offer.getResult().copy();
        result.removeTagKey(MARKER_KEY);
        return withResult(offer, result);
    }

    private static MerchantOffer withResult(MerchantOffer offer, ItemStack result) {
        return new MerchantOffer(offer.getBaseCostA(), offer.getCostB(), result, offer.getUses(), offer.getMaxUses(), offer.getXp(), offer.getPriceMultiplier());
    }

    @Nullable
    private static DeferredMap deferredMap(ItemStack stack) {
        CompoundTag marker = stack.getTagElement(MARKER_KEY);
        return marker != null ? DeferredMap.CODEC.parse(NbtOps.INSTANCE, marker).result().orElse(null) : null;
    }

    @Nullable
    private static BlockPos locate(ServerLevel level, TagKey<ConfiguredStructureFeature<?, ?>> destination, BlockPos origin) {
        return STRUCTURE_CACHE.computeIfAbsent(level, key -> new HashMap<>())
                .computeIfAbsent(new SearchKey(destination, new ChunkPos(origin)),
                        key -> Optional.ofNullable(level.findNearestMapFeature(destination, origin, SEARCH_RADIUS, false)))
                .orElse(null);
    }

    private record SearchKey(TagKey<ConfiguredStructureFeature<?, ?>> destination, ChunkPos chunk) {
    }

    private record DeferredMap(TagKey<ConfiguredStructureFeature<?, ?>> destination, MapDecoration.Type decoration, GlobalPos fallbackTarget) {
        private static final Codec<MapDecoration.Type> DECORATION_CODEC = Codec.STRING.comapFlatMap(name -> {
            try {
                return DataResult.success(MapDecoration.Type.valueOf(name));
            } catch (IllegalArgumentException e) {
                return DataResult.error("Unknown map decoration: " + name);
            }
        }, MapDecoration.Type::name);

        private static final Codec<DeferredMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TagKey.codec(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).fieldOf("destination").forGetter(DeferredMap::destination),
                DECORATION_CODEC.fieldOf("decoration").forGetter(DeferredMap::decoration),
                GlobalPos.CODEC.fieldOf("fallback_target").forGetter(DeferredMap::fallbackTarget)
        ).apply(instance, DeferredMap::new));
    }

    private record Listing(TreasureMapForEmeraldsAccessor map) implements VillagerTrades.ItemListing {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, Random random) {
            if (!(trader.level instanceof ServerLevel level)) {
                return null;
            }
            TagKey<ConfiguredStructureFeature<?, ?>> destination = map.villagertradepeek$getDestination();
            BlockPos target = locate(level, destination, trader.blockPosition());
            if (target == null) {
                return null;
            }
            ItemStack placeholder = new ItemStack(Items.FILLED_MAP);
            placeholder.setHoverName(new TranslatableComponent(map.villagertradepeek$getDisplayName()));
            placeholder.getOrCreateTag().put(MARKER_KEY, DeferredMap.CODEC
                    .encodeStart(NbtOps.INSTANCE, new DeferredMap(destination, map.villagertradepeek$getDestinationType(), GlobalPos.of(level.dimension(), target)))
                    .result()
                    .orElseThrow());
            return new MerchantOffer(new ItemStack(Items.EMERALD, map.villagertradepeek$getEmeraldCost()), new ItemStack(Items.COMPASS),
                    placeholder, map.villagertradepeek$getMaxUses(), map.villagertradepeek$getVillagerXp(), 0.2F);
        }
    }
}
