package io.github.emberbocor.villagertradepeek.client;

import io.github.emberbocor.villagertradepeek.VillagerTradePeek;
import io.github.emberbocor.villagertradepeek.network.FutureTradesPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;

public final class ClientPayloadHandler {
    private ClientPayloadHandler() {
    }

    public static void handleFutureTrades(FutureTradesPayload payload) {
        if (Minecraft.getInstance().screen instanceof MerchantScreen screen && screen.getMenu().containerId == payload.containerId()) {
            ((LockedTradesHolder) screen).villagertradepeek$setLockedTrades(payload.trades());
            VillagerTradePeek.LOGGER.debug("Received {} locked trades for container {}", payload.trades().size(), payload.containerId());
        }
    }
}
