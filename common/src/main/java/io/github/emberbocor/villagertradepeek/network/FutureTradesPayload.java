package io.github.emberbocor.villagertradepeek.network;

import java.util.List;
import java.util.stream.IntStream;

import io.github.emberbocor.villagertradepeek.VillagerTradePeek;
import io.github.emberbocor.villagertradepeek.trade.LockedTrade;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.trading.MerchantOffers;

public record FutureTradesPayload(int containerId, List<LockedTrade> trades) {
    public static final ResourceLocation ID = new ResourceLocation(VillagerTradePeek.MODID, "future_trades");

    public static FutureTradesPayload read(FriendlyByteBuf buf) {
        int containerId = buf.readVarInt();
        List<Integer> levels = buf.readList(FriendlyByteBuf::readVarInt);
        MerchantOffers offers = MerchantOffers.createFromStream(buf);
        return new FutureTradesPayload(containerId, IntStream.range(0, levels.size())
                .mapToObj(i -> new LockedTrade(levels.get(i), offers.get(i)))
                .toList());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        buf.writeCollection(trades, (out, trade) -> out.writeVarInt(trade.level()));
        MerchantOffers offers = new MerchantOffers();
        trades.forEach(trade -> offers.add(trade.offer()));
        offers.writeToStream(buf);
    }
}
