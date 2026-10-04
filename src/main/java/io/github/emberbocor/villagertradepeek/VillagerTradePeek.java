package io.github.emberbocor.villagertradepeek;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

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
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> FutureTradesCommand.register(event.getDispatcher()));
    }
}
