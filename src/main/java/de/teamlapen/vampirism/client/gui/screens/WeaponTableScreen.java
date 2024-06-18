package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.inventory.WeaponTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Gui for the weapon table. Only draws the background and the lava status
 */
public class WeaponTableScreen extends AbstractContainerScreen<WeaponTableMenu> {

    private static final ResourceLocation BACKGROUND = VResourceLocation.mod("textures/gui/container/weapon_table.png");
    private static final ResourceLocation LAVA_SPRITE = VResourceLocation.mod("container/weapon_table/lava");
    private static final ResourceLocation MISSING_LAVA_SPRITE = VResourceLocation.mod("container/weapon_table/missing_lava");

    public WeaponTableScreen(@NotNull WeaponTableMenu inventorySlotsIn, @NotNull Inventory inventoryPlayer, @NotNull Component name) {
        super(inventorySlotsIn, inventoryPlayer, name);
        this.imageWidth = 196;
        this.imageHeight = 191;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (menu.hasLava()) {
            graphics.blitSprite(LAVA_SPRITE, i+154, j+71, 24, 28);
        }
        if (menu.isMissingLava()) {
            graphics.blitSprite(MISSING_LAVA_SPRITE, i+152, j+69, 28, 32);
        }
    }

}
