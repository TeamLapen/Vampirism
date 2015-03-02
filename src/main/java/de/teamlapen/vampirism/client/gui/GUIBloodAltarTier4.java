package de.teamlapen.vampirism.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4;

/**
 * GUI for the BloodAltarTier4, currently just a placeholer
 * 
 * @author Maxanier
 *
 */
public class GUIBloodAltarTier4 extends GuiContainer {

	private static final ResourceLocation altarGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");

	private TileEntityBloodAltarTier4 tileAltar;
	public GUIBloodAltarTier4(InventoryPlayer inv, TileEntityBloodAltarTier4 tile) {
		super(tile.getNewInventoryContainer(inv));
		tileAltar = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(altarGuiTextures);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		int i1;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		String string = this.tileAltar.hasCustomInventoryName() ? this.tileAltar.getInventoryName() : I18n.format(this.tileAltar.getInventoryName(), new Object[0]);
		this.fontRendererObj.drawString(string, this.xSize / 2 - this.fontRendererObj.getStringWidth(string), 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 94, 4210752);
	}

}
