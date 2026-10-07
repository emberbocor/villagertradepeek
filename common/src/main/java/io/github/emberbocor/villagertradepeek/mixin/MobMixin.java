package io.github.emberbocor.villagertradepeek.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.villagertradepeek.trade.FutureTradesHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public abstract class MobMixin {
    @Inject(method = "convertTo", at = @At("RETURN"))
    private void villagertradepeek$copyFutureTrades(EntityType<?> entityType, boolean transferInventory, CallbackInfoReturnable<Mob> cir) {
        if ((Object) this instanceof FutureTradesHolder from && cir.getReturnValue() instanceof FutureTradesHolder to) {
            to.villagertradepeek$setFutureTrades(from.villagertradepeek$getFutureTrades());
        }
    }
}
