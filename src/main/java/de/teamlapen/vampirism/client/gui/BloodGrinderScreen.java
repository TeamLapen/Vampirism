package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.vampirism.inventory.container.BloodGrinderContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BloodGrinderScreen extends ContainerScreen<BloodGrinderContainer> {

    private static final ResourceLocation background = new ResourceLocation(REFERENCE.MODID, "textures/gui/grinder.png");
    private final Container grinderContainer;

    public BloodGrinderScreen(BloodGrinderContainer inventorySlotsIn, PlayerInventory playerInventory, ITextComponent name) {
        super(inventorySlotsIn, playerInventory, name);
        this.grinderContainer = inventorySlotsIn;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(background);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.blit(k, l, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(title.getFormattedText(), 8, 6, 0x404040);
        this.font.drawString(I18n.format("container.inventory"), 8, this.ySize - 94, 0x404040);
    }
}
