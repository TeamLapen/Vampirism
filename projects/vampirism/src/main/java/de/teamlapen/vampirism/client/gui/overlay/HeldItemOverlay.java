package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.faction.client.gui.overlay.TextureOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public abstract class HeldItemOverlay extends TextureOverlay {

    @Override
    public void render(GuiGraphicsExtractor GuiGraphicsExtractor, DeltaTracker deltaTracker) {
        ItemStack mainItem = player().getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player().getItemInHand(InteractionHand.OFF_HAND);

        renderMainHand(GuiGraphicsExtractor, deltaTracker, mainItem);

        renderOffHand(GuiGraphicsExtractor, deltaTracker, offHand);
    }

    protected abstract void renderMainHand(GuiGraphicsExtractor pGuiGraphicsExtractor, DeltaTracker deltaTracker, ItemStack stack);

    protected abstract void renderOffHand(GuiGraphicsExtractor pGuiGraphicsExtractor, DeltaTracker deltaTracker, ItemStack stack);
}
