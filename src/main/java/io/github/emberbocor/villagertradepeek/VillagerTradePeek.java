package io.github.emberbocor.villagertradepeek;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.github.emberbocor.villagertradepeek.network.ModNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(VillagerTradePeek.MODID)
public class VillagerTradePeek {
    public static final String MODID = "villagertradepeek";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VillagerTradePeek(IEventBus modEventBus) {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        modEventBus.addListener(ModNetwork::register);
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> FutureTradesCommand.register(event.getDispatcher()));
    }
}
