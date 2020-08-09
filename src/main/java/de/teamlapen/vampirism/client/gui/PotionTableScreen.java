package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.inventory.container.PotionTableContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class PotionTableScreen extends ContainerScreen<PotionTableContainer> {

    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};
    private final ResourceLocation TABLE_GUI_TEXTURES_EXTENDED = new ResourceLocation(REFERENCE.MODID, "textures/gui/potion_table_extended.png");
    private final ResourceLocation TABLE_GUI_TEXTURES = new ResourceLocation(REFERENCE.MODID, "textures/gui/potion_table.png");


    public PotionTableScreen(PotionTableContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        int cX = (this.width - this.xSize) / 2;
        int cY = (this.height - this.ySize) / 2;
        this.minecraft.getTextureManager().bindTexture(this.container.isExtendedTable() ? TABLE_GUI_TEXTURES_EXTENDED : TABLE_GUI_TEXTURES);
        this.blit(cX, cY, 0, 0, this.xSize, this.ySize);
        int fuelTime = this.container.getFuelTime();
        int fuelIconWidth = MathHelper.clamp((18 * fuelTime + 20 - 1) / 20, 0, 18);
        if (fuelIconWidth > 0) {
            this.blit(cX + 66, cY + 41, 176, 29, fuelIconWidth, 4);
        }

        int brewTime = this.container.getBrewTime();
        if (brewTime > 0) {
            int brewIconHeight = (int) (28.0F * (1.0F - (float) brewTime / 400.0F));
            if (brewIconHeight > 0) {
                this.blit(cX + 145, cY + 17, 176, 0, 9, brewIconHeight);
            }

            brewIconHeight = BUBBLELENGTHS[brewTime / 2 % 7];
            if (brewIconHeight > 0) {
                this.blit(cX + 69, cY + 14 + 26 - brewIconHeight, 185, 29 - brewIconHeight, 12, brewIconHeight);
            }
        }
    }
}
