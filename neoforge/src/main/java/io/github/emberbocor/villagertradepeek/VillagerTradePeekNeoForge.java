package io.github.emberbocor.villagertradepeek;

import io.github.emberbocor.villagertradepeek.network.NeoForgeNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(VillagerTradePeek.MODID)
public class VillagerTradePeekNeoForge {
    public VillagerTradePeekNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(NeoForgeNetwork::register);
        NeoForge.EVENT_BUS.addListener(NeoForgeNetwork::onContainerOpen);
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> FutureTradesCommand.register(event.getDispatcher()));
    }
}
