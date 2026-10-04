package io.github.emberbocor.villagertradepeek.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffers;

@Mixin(AbstractVillager.class)
public interface AbstractVillagerInvoker {
    @Invoker("addOffersFromItemListings")
    void villagertradepeek$addOffersFromItemListings(MerchantOffers offers, VillagerTrades.ItemListing[] listings, int maxNumbers);
}
