package io.github.emberbocor.villagertradepeek.trade;

import net.minecraft.world.item.trading.MerchantOffer;

public record LockedTrade(int level, MerchantOffer offer) {
}
