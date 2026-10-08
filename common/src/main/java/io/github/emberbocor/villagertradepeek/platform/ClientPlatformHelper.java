package io.github.emberbocor.villagertradepeek.platform;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface ClientPlatformHelper {
    void renderItemTooltip(Screen screen, PoseStack poseStack, List<Component> lines, ItemStack stack, int mouseX, int mouseY);
}
