package io.github.emberbocor.villagertradepeek.client;

import java.util.List;

import io.github.emberbocor.villagertradepeek.trade.LockedTrade;

public interface LockedTradesHolder {
    List<LockedTrade> villagertradepeek$getLockedTrades();

    void villagertradepeek$setLockedTrades(List<LockedTrade> trades);
}
