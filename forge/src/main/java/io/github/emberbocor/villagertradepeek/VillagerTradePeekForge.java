package io.github.emberbocor.villagertradepeek;

import io.github.emberbocor.villagertradepeek.network.ForgeNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(VillagerTradePeek.MODID)
public class VillagerTradePeekForge {
    public VillagerTradePeekForge() {
        ForgeNetwork.register();
        MinecraftForge.EVENT_BUS.addListener(ForgeNetwork::onContainerOpen);
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> FutureTradesCommand.register(event.getDispatcher()));
    }
}
