package io.github.emberbocor.villagertradepeek.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

@Mixin(VillagerTrades.TreasureMapForEmeralds.class)
public interface TreasureMapForEmeraldsAccessor {
    @Accessor("emeraldCost")
    int villagertradepeek$getEmeraldCost();

    @Accessor("destination")
    TagKey<ConfiguredStructureFeature<?, ?>> villagertradepeek$getDestination();

    @Accessor("displayName")
    String villagertradepeek$getDisplayName();

    @Accessor("destinationType")
    MapDecoration.Type villagertradepeek$getDestinationType();

    @Accessor("maxUses")
    int villagertradepeek$getMaxUses();

    @Accessor("villagerXp")
    int villagertradepeek$getVillagerXp();
}
