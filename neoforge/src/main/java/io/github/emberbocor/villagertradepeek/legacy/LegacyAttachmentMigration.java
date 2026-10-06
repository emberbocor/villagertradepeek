package io.github.emberbocor.villagertradepeek.legacy;

import java.util.Map;
import java.util.function.Supplier;

import io.github.emberbocor.villagertradepeek.VillagerTradePeek;
import io.github.emberbocor.villagertradepeek.trade.FutureTrades;
import io.github.emberbocor.villagertradepeek.trade.FutureTradesHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class LegacyAttachmentMigration {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, VillagerTradePeek.MODID);

    private static final Supplier<AttachmentType<FutureTrades>> FUTURE_TRADES = ATTACHMENT_TYPES.register("future_trades",
            () -> AttachmentType.builder(() -> new FutureTrades(VillagerProfession.NONE, Map.of()))
                    .serialize(FutureTrades.CODEC)
                    .copyOnDeath()
                    .build());

    private LegacyAttachmentMigration() {
    }

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(LegacyAttachmentMigration::onEntityJoinLevel);
    }

    private static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!event.getLevel().isClientSide() && entity instanceof FutureTradesHolder holder && entity.hasData(FUTURE_TRADES)) {
            FutureTrades legacy = entity.removeData(FUTURE_TRADES);
            if (holder.villagertradepeek$getFutureTrades() == null) {
                holder.villagertradepeek$setFutureTrades(legacy);
            }
        }
    }
}
