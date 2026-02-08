package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.faction.client.gui.overlay.TextureOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public abstract class HeldItemOverlay extends TextureOverlay {

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        ItemStack mainItem = player().getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player().getItemInHand(InteractionHand.OFF_HAND);

        renderMainHand(guiGraphics, deltaTracker, mainItem);

        renderOffHand(guiGraphics, deltaTracker, offHand);
    }

    protected abstract void renderMainHand(GuiGraphics pGuiGraphics, DeltaTracker deltaTracker, ItemStack stack);

    protected abstract void renderOffHand(GuiGraphics pGuiGraphics, DeltaTracker deltaTracker, ItemStack stack);
}
