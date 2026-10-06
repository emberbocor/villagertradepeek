package io.github.emberbocor.villagertradepeek;

import java.util.Map;
import java.util.function.Supplier;

import io.github.emberbocor.villagertradepeek.trade.FutureTrades;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, VillagerTradePeek.MODID);

    public static final Supplier<AttachmentType<FutureTrades>> FUTURE_TRADES = ATTACHMENT_TYPES.register("future_trades",
            () -> AttachmentType.builder(() -> new FutureTrades(VillagerProfession.NONE, Map.of()))
                    .serialize(FutureTrades.CODEC)
                    .copyOnDeath()
                    .build());

    private ModAttachments() {
    }
}
