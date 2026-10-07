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

import io.github.emberbocor.villagertradepeek.client.LockedTradesHolder;
import io.github.emberbocor.villagertradepeek.platform.ClientServices;
import io.github.emberbocor.villagertradepeek.trade.LockedTrade;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.RenderType;
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
    private void renderAndDecorateCostA(GuiGraphics guiGraphics, ItemStack realCost, ItemStack baseCost, int x, int y) {
        throw new AssertionError();
    }

    @Shadow
    private void renderButtonArrows(GuiGraphics guiGraphics, MerchantOffer merchantOffer, int posX, int posY) {
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
    private void villagertradepeek$disableLockedButtonsOnRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        villagertradepeek$disableLockedButtons();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void villagertradepeek$disableLockedButtonsOnClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        villagertradepeek$disableLockedButtons();
    }

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;renderScroller(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/item/trading/MerchantOffers;)V"))
    private void villagertradepeek$renderLockedTrades(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        for (int row = 0; row < VISIBLE_ROWS; row++) {
            LockedTrade trade = villagertradepeek$lockedTradeAt(row);
            if (trade != null) {
                villagertradepeek$renderLockedTrade(guiGraphics, trade.offer(), leftPos + 5, topPos + 18 + row * ROW_HEIGHT);
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void villagertradepeek$renderLockedTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        int x = leftPos + 5;
        int y = topPos + 18;
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
            guiGraphics.renderTooltip(font, unlocksAt, mouseX, mouseY);
        } else {
            List<Component> lines = new ArrayList<>(getTooltipFromContainerItem(stack));
            lines.add(unlocksAt);
            ClientServices.PLATFORM.renderItemTooltip(guiGraphics, font, lines, stack, mouseX, mouseY);
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
    private void villagertradepeek$renderLockedTrade(GuiGraphics guiGraphics, MerchantOffer offer, int x, int y) {
        int itemY = y + 1;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        renderAndDecorateCostA(guiGraphics, offer.getCostA(), offer.getBaseCostA(), x + 5, itemY);
        villagertradepeek$renderItem(guiGraphics, offer.getCostB(), x + 35, itemY);
        renderButtonArrows(guiGraphics, offer, leftPos, itemY);
        villagertradepeek$renderItem(guiGraphics, offer.getResult(), x + 68, itemY);
        guiGraphics.pose().popPose();
        guiGraphics.fill(RenderType.guiOverlay(), x, y, x + ROW_WIDTH, y + ROW_HEIGHT, LOCKED_OVERLAY_COLOR);
    }

    @Unique
    private void villagertradepeek$renderItem(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
        guiGraphics.renderFakeItem(stack, x, y);
        guiGraphics.renderItemDecorations(font, stack, x, y);
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
