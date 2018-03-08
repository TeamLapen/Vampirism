package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBloodGrinder extends GuiContainer {

    private static final ResourceLocation background = new ResourceLocation(REFERENCE.MODID, "textures/gui/grinder.png");
    private final Container grinderContainer;

    public GuiBloodGrinder(Container inventorySlotsIn) {
        super(inventorySlotsIn);
        this.grinderContainer = inventorySlotsIn;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(ModBlocks.blood_grinder.getLocalizedName(), 8, 6, 0x404040);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 94, 0x404040);
    }
}
