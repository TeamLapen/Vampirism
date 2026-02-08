package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.items.IBloodChargeable;
import de.teamlapen.vampirism.common.world.items.VampireSwordItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class BloodChargeOverlay extends HeldItemOverlay {

    public static final Identifier OUTER_BAR = VIdentifier.mod("hud/sword_charge_bar");
    public static final Identifier OUTER_BAR_BIG = VIdentifier.mod("hud/sword_charge_bar_big");
    public static final Identifier INNER_BAR_BLOOD = VIdentifier.mod( "hud/sword_charge_bar_blood");
    public static final Identifier INNER_BAR_EXPERIENCE = VIdentifier.mod( "hud/sword_charge_bar_experience");
    private static final int OUTER_WIDTH = 100;
    private static final int OUTER_HEIGHT = 15;
    private static final int OUTER_BIG_HEIGHT = 21;
    private static final int INNER_WIDTH_DIFF = 12;
    private static final int INNER_HEIGHT_DIFF = 10;

    @Override
    protected void renderMainHand(GuiGraphics pGuiGraphics, DeltaTracker deltaTracker, ItemStack stack) {
        int x = pGuiGraphics.guiWidth() / 2 + 91;
        x = (x + (pGuiGraphics.guiWidth() - x) / 2) - OUTER_WIDTH /2;
        render(pGuiGraphics, stack, x, pGuiGraphics.guiHeight() - 45);
    }

    @Override
    protected void renderOffHand(GuiGraphics pGuiGraphics, DeltaTracker deltaTracker, ItemStack stack) {
        render(pGuiGraphics, stack, (pGuiGraphics.guiWidth() / 2 - 91) / 2 - OUTER_WIDTH /2, pGuiGraphics.guiHeight() - 45);
    }

    private void render(GuiGraphics pGuiGraphics, ItemStack stack, int x, int y) {
        if (stack.getItem() instanceof IBloodChargeable item) {

            if (item instanceof VampireSwordItem sword && sword.getTrained(stack) < 0.99) {
                pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, OUTER_BAR_BIG, x, y + (OUTER_HEIGHT - OUTER_BIG_HEIGHT), OUTER_WIDTH, OUTER_BIG_HEIGHT);
                pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, INNER_BAR_BLOOD, x + INNER_WIDTH_DIFF / 2, y + INNER_HEIGHT_DIFF / 2, (int) ((OUTER_WIDTH - INNER_WIDTH_DIFF) * item.getChargePercentage(stack)), OUTER_HEIGHT - INNER_HEIGHT_DIFF);
                pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, INNER_BAR_EXPERIENCE, x + INNER_WIDTH_DIFF / 2, y + INNER_HEIGHT_DIFF / 2 + (OUTER_HEIGHT - OUTER_BIG_HEIGHT), (int) ((OUTER_WIDTH - INNER_WIDTH_DIFF) * sword.getTrained(stack)), OUTER_HEIGHT - INNER_HEIGHT_DIFF);
            } else {
                pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, OUTER_BAR, x, y, OUTER_WIDTH, OUTER_HEIGHT);
                pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, INNER_BAR_BLOOD, x+ INNER_WIDTH_DIFF / 2, y + INNER_HEIGHT_DIFF / 2, (int) ((OUTER_WIDTH - INNER_WIDTH_DIFF) * item.getChargePercentage(stack)), OUTER_HEIGHT - INNER_HEIGHT_DIFF);
            }
        }
    }
}
