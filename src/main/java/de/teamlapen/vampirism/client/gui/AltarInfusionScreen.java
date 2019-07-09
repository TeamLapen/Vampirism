package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.tileentity.TileAltarInfusion;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AltarInfusionScreen extends ContainerScreen {

    private static final ResourceLocation altarGuiTextures = new ResourceLocation(REFERENCE.MODID, "textures/gui/altar4.png");

    private TileAltarInfusion tileAltar;

    public AltarInfusionScreen(PlayerInventory inv, TileAltarInfusion tile) {
        super(tile.getNewInventoryContainer(inv));
        tileAltar = tile;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(altarGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        String string = this.tileAltar.hasCustomName() ? this.tileAltar.getName().getString() : I18n.format(this.tileAltar.getName().getString());
        this.fontRenderer.drawString(string, this.xSize / 2 - this.fontRenderer.getStringWidth(string) / 2 + 6, 6, 4210752);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 94, 4210752);
    }

}