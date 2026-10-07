package io.github.emberbocor.villagertradepeek.trade;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradepeek.VillagerTradePeek;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;

public final class FutureTradeStorage {
    private static final String NBT_KEY = VillagerTradePeek.MODID + ":future_trades";

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

    public static void save(Entity entity, @Nullable FutureTrades trades, CompoundTag tag) {
        if (trades != null) {
            FutureTrades.CODEC.encodeStart(entity.registryAccess().createSerializationContext(NbtOps.INSTANCE), trades)
                    .resultOrPartial(Util.prefix("Failed to save future trades: ", VillagerTradePeek.LOGGER::error))
                    .ifPresent(encoded -> tag.put(NBT_KEY, encoded));
        }
    }

    public static void load(Entity entity, CompoundTag tag) {
        if (tag.contains(NBT_KEY)) {
            ((FutureTradesHolder) entity).villagertradepeek$setFutureTrades(FutureTrades.CODEC
                    .parse(entity.registryAccess().createSerializationContext(NbtOps.INSTANCE), tag.get(NBT_KEY))
                    .resultOrPartial(Util.prefix("Failed to load future trades: ", VillagerTradePeek.LOGGER::warn))
                    .orElse(null));
        }
    }
}
