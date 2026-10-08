package io.github.emberbocor.villagertradepeek.network;

import java.util.List;

import io.github.emberbocor.villagertradepeek.VillagerTradePeek;
import io.github.emberbocor.villagertradepeek.trade.LockedTrade;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record FutureTradesPayload(int containerId, List<LockedTrade> trades) implements CustomPacketPayload {
    public static final Type<FutureTradesPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(VillagerTradePeek.MODID, "future_trades"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FutureTradesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, FutureTradesPayload::containerId,
            LockedTrade.STREAM_CODEC.apply(ByteBufCodecs.list()), FutureTradesPayload::trades,
            FutureTradesPayload::new);

    @Override
    public Type<FutureTradesPayload> type() {
        return TYPE;
    }
}
