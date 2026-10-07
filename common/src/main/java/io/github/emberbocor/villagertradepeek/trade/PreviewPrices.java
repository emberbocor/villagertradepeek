package io.github.emberbocor.villagertradepeek.trade;

import java.util.List;
import java.util.stream.IntStream;

import io.github.emberbocor.villagertradepeek.mixin.VillagerInvoker;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class PreviewPrices {
    private PreviewPrices() {
    }

    public static List<LockedTrade> withSpecialPrices(Villager villager, Player player, List<LockedTrade> trades) {
        MerchantOffers preview = new MerchantOffers();
        trades.forEach(trade -> preview.add(new MerchantOffer(trade.offer().createTag())));
        MerchantOffers offers = villager.getOffers();
        villager.setOffers(preview);
        try {
            ((VillagerInvoker) villager).villagertradepeek$updateSpecialPrices(player);
        } finally {
            villager.setOffers(offers);
        }
        return IntStream.range(0, trades.size())
                .mapToObj(i -> new LockedTrade(trades.get(i).level(), preview.get(i)))
                .toList();
    }
}
