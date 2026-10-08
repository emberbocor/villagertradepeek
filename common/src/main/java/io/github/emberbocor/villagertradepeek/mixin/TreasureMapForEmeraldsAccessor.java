package io.github.emberbocor.villagertradepeek.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

@Mixin(VillagerTrades.TreasureMapForEmeralds.class)
public interface TreasureMapForEmeraldsAccessor {
    @Accessor("emeraldCost")
    int villagertradepeek$getEmeraldCost();

    @Accessor("destination")
    TagKey<Structure> villagertradepeek$getDestination();

    @Accessor("displayName")
    String villagertradepeek$getDisplayName();

    @Accessor("destinationType")
    Holder<MapDecorationType> villagertradepeek$getDestinationType();

    @Accessor("maxUses")
    int villagertradepeek$getMaxUses();

    @Accessor("villagerXp")
    int villagertradepeek$getVillagerXp();
}
