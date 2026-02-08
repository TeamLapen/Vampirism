package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.items.IBloodChargeable;
import de.teamlapen.vampirism.common.world.items.VampireSwordItem;
import de.teamlapen.vampirism.common.world.items.component.AppliedOilContent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class ItemChargeOverlay extends HeldItemOverlay {

    private static final Identifier OUTER_BAR = VIdentifier.mod("hud/charge_bar");
    private static final Identifier OUTER_BAR_BIG = VIdentifier.mod("hud/charge_bar_big");
    private static final Identifier INNER_BAR_BLOOD = VIdentifier.mod( "hud/charge_bar_blood");
    private static final Identifier INNER_BAR_EXPERIENCE = VIdentifier.mod( "hud/charge_bar_experience");
    private static final Identifier INNER_BAR_OIL = VIdentifier.mod( "hud/charge_bar_oil");
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
        } else {
            AppliedOilContent oil = AppliedOilContent.getAppliedOil(stack).orElse(null);
            if (oil != null && oil.duration() > 0) {
                float percentage = (float) oil.duration() / oil.oil().value().getMaxDuration(stack);
                pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, OUTER_BAR, x, y, OUTER_WIDTH, OUTER_HEIGHT);
                pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, INNER_BAR_OIL, x+ INNER_WIDTH_DIFF / 2, y + INNER_HEIGHT_DIFF / 2, (int) ((OUTER_WIDTH - INNER_WIDTH_DIFF) * percentage), OUTER_HEIGHT - INNER_HEIGHT_DIFF);
            }
        }
    }
}
