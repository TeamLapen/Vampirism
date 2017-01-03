package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.vampirism.inventory.AlchemicalCauldronContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * 1.10
 *
 * @author maxanier
 */
public class GuiAlchemicalCauldron extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("vampirism:textures/gui/alchemicalCauldron.png");

    public GuiAlchemicalCauldron(InventoryPlayer inventoryPlayer, InventorySlot.IInventorySlotInventory tile) {
        super(new AlchemicalCauldronContainer(inventoryPlayer, tile));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}
