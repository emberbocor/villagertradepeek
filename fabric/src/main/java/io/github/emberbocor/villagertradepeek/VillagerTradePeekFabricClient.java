package io.github.emberbocor.villagertradepeek;

import io.github.emberbocor.villagertradepeek.client.ClientPayloadHandler;
import io.github.emberbocor.villagertradepeek.network.FutureTradesPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class VillagerTradePeekFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(FutureTradesPayload.ID, (client, handler, buf, responseSender) -> {
            FutureTradesPayload payload = FutureTradesPayload.read(buf);
            client.execute(() -> ClientPayloadHandler.handleFutureTrades(payload));
        });
    }
}
