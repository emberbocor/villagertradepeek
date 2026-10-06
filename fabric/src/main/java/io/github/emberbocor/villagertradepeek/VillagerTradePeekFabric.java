package io.github.emberbocor.villagertradepeek;

import io.github.emberbocor.villagertradepeek.network.FutureTradesPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class VillagerTradePeekFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(FutureTradesPayload.TYPE, FutureTradesPayload.STREAM_CODEC);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> FutureTradesCommand.register(dispatcher));
    }
}
