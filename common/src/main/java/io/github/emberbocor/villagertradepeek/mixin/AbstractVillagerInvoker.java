package io.github.emberbocor.villagertradepeek.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffers;

@Mixin(AbstractVillager.class)
public interface AbstractVillagerInvoker {
    @Invoker("addOffersFromItemListings")
    void villagertradepeek$addOffersFromItemListings(ServerLevel level, MerchantOffers offers, VillagerTrades.ItemListing[] listings, int slots);
}
