package io.github.emberbocor.villagertradepeek.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

@Mixin(Villager.class)
public interface VillagerInvoker {
    @Invoker("updateSpecialPrices")
    void villagertradepeek$updateSpecialPrices(Player player);
}
