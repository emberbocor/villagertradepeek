package io.github.emberbocor.villagertradepeek;

import java.util.List;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import io.github.emberbocor.villagertradepeek.platform.Services;
import io.github.emberbocor.villagertradepeek.trade.FutureTradeStorage;
import io.github.emberbocor.villagertradepeek.trade.FutureTrades;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class FutureTradesCommand {
    private static final SimpleCommandExceptionType NOT_A_VILLAGER = new SimpleCommandExceptionType(Component.literal("Target is not a villager"));
    private static final SimpleCommandExceptionType NOT_LEVEL_ONE = new SimpleCommandExceptionType(Component.literal("Villager is not level 1"));

    private FutureTradesCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(VillagerTradePeek.MODID)
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("future")
                        .then(Commands.argument("villager", EntityArgument.entity())
                                .executes(context -> print(context.getSource(), villager(EntityArgument.getEntity(context, "villager"))))));
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            root.then(Commands.literal("reroll")
                    .then(Commands.argument("villager", EntityArgument.entity())
                            .executes(context -> reroll(context.getSource(), villager(EntityArgument.getEntity(context, "villager"))))));
        }
        dispatcher.register(root);
    }

    private static Villager villager(Entity entity) throws CommandSyntaxException {
        if (entity instanceof Villager villager) {
            return villager;
        }
        throw NOT_A_VILLAGER.create();
    }

    private static int print(CommandSourceStack source, Villager villager) {
        FutureTrades stored = FutureTradeStorage.get(villager).orElse(null);
        if (stored == null) {
            source.sendSuccess(() -> Component.literal("No future trades stored"), false);
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Future trades for " + BuiltInRegistries.VILLAGER_PROFESSION.getKey(stored.profession())), false);
        stored.levels().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> printLevel(source, entry.getKey(), entry.getValue()));
        return stored.levels().size();
    }

    private static int reroll(CommandSourceStack source, Villager villager) throws CommandSyntaxException {
        if (villager.getVillagerData().getLevel() != VillagerData.MIN_VILLAGER_LEVEL) {
            throw NOT_LEVEL_ONE.create();
        }
        villager.setOffers(null);
        MerchantOffers offers = villager.getOffers();
        if (villager.getTradingPlayer() instanceof ServerPlayer player) {
            player.sendMerchantOffers(player.containerMenu.containerId, offers, VillagerData.MIN_VILLAGER_LEVEL,
                    villager.getVillagerXp(), villager.showProgressBar(), villager.canRestock());
        }
        source.sendSuccess(() -> Component.literal("Rerolled level 1 trades"), true);
        return 1;
    }

    private static void printLevel(CommandSourceStack source, int level, List<MerchantOffer> offers) {
        source.sendSuccess(() -> Component.literal("Level " + level + ":"), false);
        for (MerchantOffer offer : offers) {
            source.sendSuccess(() -> describe(offer), false);
        }
    }

    private static Component describe(MerchantOffer offer) {
        MutableComponent line = Component.literal("  ").append(describe(offer.getBaseCostA()));
        if (!offer.getCostB().isEmpty()) {
            line.append(" + ").append(describe(offer.getCostB()));
        }
        return line.append(" -> ").append(describe(offer.getResult()));
    }

    private static Component describe(ItemStack stack) {
        return Component.literal(stack.getCount() + "x ").append(stack.getDisplayName());
    }
}
