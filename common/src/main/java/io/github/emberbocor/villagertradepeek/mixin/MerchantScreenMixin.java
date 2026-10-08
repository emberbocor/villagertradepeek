package io.github.emberbocor.villagertradepeek.mixin;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.emberbocor.villagertradepeek.client.LockedTradesHolder;
import io.github.emberbocor.villagertradepeek.platform.ClientServices;
import io.github.emberbocor.villagertradepeek.trade.LockedTrade;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> implements LockedTradesHolder {
    @Unique
    private static final int VISIBLE_ROWS = 7;
    @Unique
    private static final int ROW_WIDTH = 88;
    @Unique
    private static final int ROW_HEIGHT = 20;
    @Unique
    private static final int LOCKED_OVERLAY_COLOR = 0x80303030;
    @Unique
    private static final float ROW_ITEM_BLIT_OFFSET = 100.0F;
    @Unique
    private static final double LOCKED_OVERLAY_Z = 310.0D;

    @Shadow
    @Final
    private MerchantScreen.TradeOfferButton[] tradeOfferButtons;
    @Shadow
    int scrollOff;

    @Unique
    private List<LockedTrade> villagertradepeek$lockedTrades = List.of();

    private MerchantScreenMixin(MerchantMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Shadow
    private void renderAndDecorateCostA(PoseStack poseStack, ItemStack realCost, ItemStack baseCost, int x, int y) {
        throw new AssertionError();
    }

    @Shadow
    private void renderButtonArrows(PoseStack poseStack, MerchantOffer merchantOffer, int posX, int posY) {
        throw new AssertionError();
    }

    @Override
    public List<LockedTrade> villagertradepeek$getLockedTrades() {
        return villagertradepeek$lockedTrades;
    }

    @Override
    public void villagertradepeek$setLockedTrades(List<LockedTrade> trades) {
        villagertradepeek$lockedTrades = trades;
        scrollOff = Mth.clamp(scrollOff, 0, Math.max(0, menu.getOffers().size() + trades.size() - VISIBLE_ROWS));
    }

    @ModifyExpressionValue(method = {"render", "renderScroller", "mouseScrolled", "mouseDragged", "mouseClicked"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffers;size()I"))
    private int villagertradepeek$includeLockedTrades(int size) {
        return size + villagertradepeek$lockedTrades.size();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void villagertradepeek$disableLockedButtonsOnRender(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        villagertradepeek$disableLockedButtons();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void villagertradepeek$disableLockedButtonsOnClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        villagertradepeek$disableLockedButtons();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void villagertradepeek$renderLockedTrades(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        int x = leftPos + 5;
        int y = topPos + 18;
        if (!menu.getOffers().isEmpty()) {
            villagertradepeek$renderLockedRows(poseStack, x, y);
        }
        villagertradepeek$renderLockedTooltip(poseStack, mouseX, mouseY, x, y);
    }

    @Unique
    private void villagertradepeek$renderLockedRows(PoseStack poseStack, int x, int y) {
        itemRenderer.blitOffset = ROW_ITEM_BLIT_OFFSET;
        for (int row = 0; row < VISIBLE_ROWS; row++) {
            LockedTrade trade = villagertradepeek$lockedTradeAt(row);
            if (trade != null) {
                villagertradepeek$renderLockedTrade(poseStack, trade.offer(), x, y + row * ROW_HEIGHT);
            }
        }
        itemRenderer.blitOffset = 0.0F;
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, LOCKED_OVERLAY_Z);
        for (int row = 0; row < VISIBLE_ROWS; row++) {
            if (villagertradepeek$lockedTradeAt(row) != null) {
                fill(poseStack, x, y + row * ROW_HEIGHT, x + ROW_WIDTH, y + (row + 1) * ROW_HEIGHT, LOCKED_OVERLAY_COLOR);
            }
        }
        poseStack.popPose();
    }

    @Unique
    private void villagertradepeek$renderLockedTooltip(PoseStack poseStack, int mouseX, int mouseY, int x, int y) {
        if (mouseX < x || mouseX >= x + ROW_WIDTH || mouseY < y) {
            return;
        }
        LockedTrade trade = villagertradepeek$lockedTradeAt((mouseY - y) / ROW_HEIGHT);
        if (trade == null) {
            return;
        }
        Component unlocksAt = Component.translatable("villagertradepeek.tooltip.unlocks_at", Component.translatable("merchant.level." + trade.level()))
                .withStyle(ChatFormatting.YELLOW);
        ItemStack stack = villagertradepeek$hoveredStack(trade.offer(), mouseX - x);
        if (stack.isEmpty()) {
            renderTooltip(poseStack, unlocksAt, mouseX, mouseY);
        } else {
            List<Component> lines = new ArrayList<>(getTooltipFromItem(stack));
            lines.add(unlocksAt);
            ClientServices.PLATFORM.renderItemTooltip(this, poseStack, lines, stack, mouseX, mouseY);
        }
    }

    @Unique
    private void villagertradepeek$disableLockedButtons() {
        int offerCount = menu.getOffers().size();
        for (MerchantScreen.TradeOfferButton button : tradeOfferButtons) {
            button.active = button.getIndex() + scrollOff < offerCount;
        }
    }

    @Unique
    @Nullable
    private LockedTrade villagertradepeek$lockedTradeAt(int row) {
        if (row >= VISIBLE_ROWS) {
            return null;
        }
        int offerCount = menu.getOffers().size();
        int firstIndex = offerCount + villagertradepeek$lockedTrades.size() > VISIBLE_ROWS ? scrollOff : 0;
        int lockedIndex = firstIndex + row - offerCount;
        return lockedIndex >= 0 && lockedIndex < villagertradepeek$lockedTrades.size() ? villagertradepeek$lockedTrades.get(lockedIndex) : null;
    }

    @Unique
    private void villagertradepeek$renderLockedTrade(PoseStack poseStack, MerchantOffer offer, int x, int y) {
        int itemY = y + 1;
        renderAndDecorateCostA(poseStack, offer.getCostA(), offer.getBaseCostA(), x + 5, itemY);
        villagertradepeek$renderItem(offer.getCostB(), x + 35, itemY);
        renderButtonArrows(poseStack, offer, leftPos, itemY);
        villagertradepeek$renderItem(offer.getResult(), x + 68, itemY);
    }

    @Unique
    private void villagertradepeek$renderItem(ItemStack stack, int x, int y) {
        itemRenderer.renderAndDecorateFakeItem(stack, x, y);
        itemRenderer.renderGuiItemDecorations(font, stack, x, y);
    }

    @Unique
    private static ItemStack villagertradepeek$hoveredStack(MerchantOffer offer, int offsetX) {
        if (offsetX < 20) {
            return offer.getCostA();
        }
        if (offsetX > 30 && offsetX < 50) {
            return offer.getCostB();
        }
        return offsetX > 65 ? offer.getResult() : ItemStack.EMPTY;
    }
}
