package io.github.emberbocor.villagertradepeek.trade;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.emberbocor.villagertradepeek.VillagerTradePeek;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class FutureTradeStorage {
    private static final String NBT_KEY = VillagerTradePeek.MODID + ":future_trades";
    private static final String DATA_VERSION_KEY = "data_version";

    private FutureTradeStorage() {
    }

    public static Optional<FutureTrades> get(Villager villager) {
        return Optional.ofNullable(((FutureTradesHolder) villager).villagertradepeek$getFutureTrades());
    }

    public static void set(Villager villager, FutureTrades trades) {
        ((FutureTradesHolder) villager).villagertradepeek$setFutureTrades(trades);
    }

    public static void remove(Villager villager) {
        ((FutureTradesHolder) villager).villagertradepeek$setFutureTrades(null);
    }

    public static void save(ValueOutput output, @Nullable FutureTrades trades) {
        if (trades != null) {
            output.store(NBT_KEY, StoredTrades.CODEC, new StoredTrades(currentDataVersion(), trades));
        }
    }

    @Nullable
    public static FutureTrades load(ValueInput input, HolderLookup.Provider registries) {
        return input.read(NBT_KEY, CompoundTag.CODEC).map(tag -> decode(registries, tag)).orElse(null);
    }

    @Nullable
    private static FutureTrades decode(HolderLookup.Provider registries, CompoundTag tag) {
        if (tag.getIntOr(DATA_VERSION_KEY, 0) != currentDataVersion()) {
            VillagerTradePeek.LOGGER.debug("Discarding future trades saved by another game version");
            return null;
        }
        return StoredTrades.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag)
                .ifError(error -> VillagerTradePeek.LOGGER.debug("Discarding unreadable future trades: {}", error.message()))
                .result()
                .map(StoredTrades::trades)
                .orElse(null);
    }

    private static int currentDataVersion() {
        return SharedConstants.getCurrentVersion().dataVersion().version();
    }

    private record StoredTrades(int dataVersion, FutureTrades trades) {
        private static final Codec<StoredTrades> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf(DATA_VERSION_KEY).forGetter(StoredTrades::dataVersion),
                FutureTrades.MAP_CODEC.forGetter(StoredTrades::trades)
        ).apply(instance, StoredTrades::new));
    }
}
