package io.github.emberbocor.villagertradepeek.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import io.github.emberbocor.villagertradepeek.client.LockedTradesHolder;
import io.github.emberbocor.villagertradepeek.trade.LockedTrade;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin implements LockedTradesHolder {
    @Unique
    private List<LockedTrade> villagertradepeek$lockedTrades = List.of();

    @Override
    public List<LockedTrade> villagertradepeek$getLockedTrades() {
        return villagertradepeek$lockedTrades;
    }

    @Override
    public void villagertradepeek$setLockedTrades(List<LockedTrade> trades) {
        villagertradepeek$lockedTrades = trades;
    }
}
