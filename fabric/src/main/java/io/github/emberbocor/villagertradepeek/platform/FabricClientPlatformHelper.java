package io.github.emberbocor.villagertradepeek.platform;

import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class FabricClientPlatformHelper implements ClientPlatformHelper {
    @Override
    public void renderItemTooltip(GuiGraphics guiGraphics, Font font, List<Component> lines, ItemStack stack, int mouseX, int mouseY) {
        guiGraphics.setTooltipForNextFrame(font, lines, stack.getTooltipImage(), mouseX, mouseY, stack.get(DataComponents.TOOLTIP_STYLE));
    }
}
