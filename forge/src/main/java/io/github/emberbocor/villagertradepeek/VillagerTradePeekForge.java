package io.github.emberbocor.villagertradepeek;

import io.github.emberbocor.villagertradepeek.network.ForgeNetwork;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(VillagerTradePeek.MODID)
public class VillagerTradePeekForge {
    public VillagerTradePeekForge() {
        ForgeNetwork.register();
        PlayerContainerEvent.Open.BUS.addListener(ForgeNetwork::onContainerOpen);
        RegisterCommandsEvent.BUS.addListener(event -> FutureTradesCommand.register(event.getDispatcher()));
    }
}
