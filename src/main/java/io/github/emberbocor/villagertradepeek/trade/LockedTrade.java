package io.github.emberbocor.villagertradepeek.trade;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.trading.MerchantOffer;

public record LockedTrade(int level, MerchantOffer offer) {
    public static final StreamCodec<RegistryFriendlyByteBuf, LockedTrade> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, LockedTrade::level,
            MerchantOffer.STREAM_CODEC, LockedTrade::offer,
            LockedTrade::new);
}
