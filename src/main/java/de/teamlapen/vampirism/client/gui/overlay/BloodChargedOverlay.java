package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BloodChargedOverlay extends HeldItemOverlay {

    public static final ResourceLocation OUTER = VResourceLocation.mod("widget/blood_bar");
    public static final ResourceLocation INNER = VResourceLocation.mod( "widget/blood_bar_content");
    private static final int WIDTH = 90;
    private static final int HEIGHT = 12;

    @Override
    protected void renderMainHand(@NotNull GuiGraphics pGuiGraphics, @NotNull DeltaTracker deltaTracker, ItemStack stack) {
        if (stack.getItem() instanceof IBloodChargeable item) {
            int y = pGuiGraphics.guiHeight() - 45;
            int x = pGuiGraphics.guiWidth() / 2 + 91;
            x = (x + (pGuiGraphics.guiWidth() - x) / 2) - WIDTH/2;
            pGuiGraphics.blitSprite(OUTER, x, y, WIDTH, HEIGHT);
            pGuiGraphics.blitSprite(INNER, x, y, (int) (WIDTH * item.getChargePercentage(stack)), HEIGHT);
        }
    }

    @Override
    protected void renderOffHand(@NotNull GuiGraphics pGuiGraphics, @NotNull DeltaTracker deltaTracker, ItemStack stack) {
        if (stack.getItem() instanceof IBloodChargeable item) {
            int y = pGuiGraphics.guiHeight() - 45;
            int x = (pGuiGraphics.guiWidth() / 2 - 91) / 2 - WIDTH/2;
            pGuiGraphics.blitSprite(OUTER, x, y, WIDTH, HEIGHT);
            pGuiGraphics.blitSprite(INNER, x, y, (int) (WIDTH * item.getChargePercentage(stack)), HEIGHT);
        }
    }
}
