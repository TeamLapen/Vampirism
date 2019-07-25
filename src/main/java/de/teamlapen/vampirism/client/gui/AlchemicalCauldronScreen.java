package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.container.AlchemicalCauldronContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class AlchemicalCauldronScreen extends ContainerScreen<AlchemicalCauldronContainer> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("vampirism:textures/gui/alchemical_cauldron.png");

    public AlchemicalCauldronScreen(AlchemicalCauldronContainer inventorySlotsIn, PlayerInventory inventoryPlayer, ITextComponent name) {
        super(inventorySlotsIn, inventoryPlayer, name);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);

        int k = container.getBurnLeftScaled();
        if (k > 0) this.blit(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);

        int l = container.getCookProgressionScaled();
        this.blit(i + 79, j + 34, 176, 14, l + 1, 16);
        l = l / 24 * 30;
        this.blit(i + 142, j + 28 + 30 - l, 176, 60 - l, 12, l);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = new TranslationTextComponent("tile.vampirism.alchemical_cauldron.display", Minecraft.getInstance().player.getDisplayName().applyTextStyle(TextFormatting.DARK_BLUE), ModBlocks.alchemical_cauldron.getNameTextComponent()).getFormattedText();
        this.font.drawString(name, 5, 6.0F, 0x404040);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 94), 0x404040);
    }

}
