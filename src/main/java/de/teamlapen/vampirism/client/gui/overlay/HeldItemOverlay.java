package de.teamlapen.vampirism.client.gui.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class HeldItemOverlay implements LayeredDraw.Layer {

    protected final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, @NotNull DeltaTracker deltaTracker) {
        LocalPlayer player = mc.player;
        ItemStack mainItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

        renderMainHand(pGuiGraphics, deltaTracker, mainItem);

        renderOffHand(pGuiGraphics, deltaTracker, offHand);
    }

    protected abstract void renderMainHand(@NotNull GuiGraphics pGuiGraphics, @NotNull DeltaTracker deltaTracker, ItemStack stack);

    protected abstract void renderOffHand(@NotNull GuiGraphics pGuiGraphics, @NotNull DeltaTracker deltaTracker, ItemStack stack);
}
