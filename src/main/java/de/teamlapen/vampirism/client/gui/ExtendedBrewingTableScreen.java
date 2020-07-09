package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.inventory.container.ExtendedPotionTableContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ExtendedBrewingTableScreen extends ContainerScreen<ExtendedPotionTableContainer> {

    private final ResourceLocation TABLE_GUI_TEXTURES_EXTENDED = new ResourceLocation(REFERENCE.MODID, "textures/gui/blood_potion_table.png");
    private final ResourceLocation TABLE_GUI_TEXTURES = new ResourceLocation(REFERENCE.MODID, "textures/gui/blood_potion_table.png");

    public ExtendedBrewingTableScreen(ExtendedPotionTableContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.minecraft.getTextureManager().bindTexture(this.container.isExtendedTable() ? TABLE_GUI_TEXTURES_EXTENDED : TABLE_GUI_TEXTURES);
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
    }
}
