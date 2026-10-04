package io.github.emberbocor.villagertradepeek.trade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffer;

public record FutureTrades(VillagerProfession profession, Map<Integer, List<MerchantOffer>> levels) {
    private static final Codec<Integer> LEVEL_KEY_CODEC = Codec.STRING.comapFlatMap(key -> {
        try {
            return DataResult.success(Integer.parseInt(key));
        } catch (NumberFormatException e) {
            return DataResult.error(() -> "Invalid villager level: " + key);
        }
    }, String::valueOf);

    public static final Codec<FutureTrades> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.VILLAGER_PROFESSION.byNameCodec().fieldOf("profession").forGetter(FutureTrades::profession),
            Codec.unboundedMap(LEVEL_KEY_CODEC, MerchantOffer.CODEC.listOf()).fieldOf("levels").forGetter(FutureTrades::levels)
    ).apply(instance, FutureTrades::new));

    public FutureTrades {
        levels = Map.copyOf(levels);
    }

    public FutureTrades withoutLevel(int level) {
        Map<Integer, List<MerchantOffer>> remaining = new HashMap<>(levels);
        remaining.remove(level);
        return new FutureTrades(profession, remaining);
    }

    public boolean isEmpty() {
        return levels.values().stream().allMatch(List::isEmpty);
    }

    public List<LockedTrade> lockedTrades() {
        return levels.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> entry.getValue().stream().map(offer -> new LockedTrade(entry.getKey(), offer)))
                .toList();
    }
}
