package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.items.crossbow.TechCrossbowItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;

import java.util.List;

public class TechCrossbowChargedOverlay extends HeldItemOverlay {
    public static final Identifier OUTER = VIdentifier.mod("widget/arrow");
    public static final Identifier INNER = VIdentifier.mc("widget/arrows");
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    protected void renderMainHand(GuiGraphics pGuiGraphics, DeltaTracker deltaTracker, ItemStack stack) {
        if (stack.getItem() instanceof TechCrossbowItem) {
            List<ItemStack> projectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).getItems();
            int x = pGuiGraphics.guiWidth() - 30;
            int y = pGuiGraphics.guiHeight() - 60;
            pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, OUTER, x, y, 20, 20);
            if (!projectiles.isEmpty()) {
                pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, INNER, 15,37 , 0,0, x + 3, y - 3 * projectiles.size() + 13, 15, 3 * projectiles.size());
            }
            pGuiGraphics.drawString(mc.font, String.valueOf(projectiles.size()), x + 8, y + 12, -1, false);
        }
    }

    @Override
    protected void renderOffHand(GuiGraphics pGuiGraphics, DeltaTracker deltaTracker, ItemStack stack) {
        if (stack.getItem() instanceof TechCrossbowItem) {
            List<ItemStack> projectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).getItems();
            int x = 15;
            int y = pGuiGraphics.guiHeight() - 60;
            pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, OUTER, x, y, 20, 20);
            if (!projectiles.isEmpty()) {
                pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, INNER, 15,37 , 0,0, x + 3, y - 3 * projectiles.size() + 13, 15, 3 * projectiles.size());
            }
            pGuiGraphics.drawString(mc.font, String.valueOf(projectiles.size()), x + 8, y + 12, -1, false);
        }
    }
}
