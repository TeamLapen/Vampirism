package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.tileentity.TileAltarInfusion;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiAltarInfusion extends GuiContainer {

    private static final ResourceLocation altarGuiTextures = new ResourceLocation(REFERENCE.MODID, "textures/gui/altar4.png");

    private TileAltarInfusion tileAltar;

    public GuiAltarInfusion(InventoryPlayer inv, TileAltarInfusion tile) {
        super(tile.getNewInventoryContainer(inv));
        tileAltar = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(altarGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        String string = this.tileAltar.hasCustomName() ? this.tileAltar.getName() : I18n.format(this.tileAltar.getName());
        this.fontRendererObj.drawString(string, this.xSize / 2 - this.fontRendererObj.getStringWidth(string) / 2 + 6, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 94, 4210752);
    }

}