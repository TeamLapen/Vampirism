package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.inventory.container.WeaponTableContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Gui for the weapon table. Only draws the background and the lava status
 */
@OnlyIn(Dist.CLIENT)
public class WeaponTableScreen extends ContainerScreen<WeaponTableContainer> {

    private static final ResourceLocation TABLE_GUI_TEXTURES = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table.png");
    private static final ResourceLocation TABLE_GUI_TEXTURES_LAVA = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_lava.png");
    private static final ResourceLocation TABLE_GUI_TEXTURES_MISSING_LAVA = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_missing_lava.png");
    private int lava = 0;

    public WeaponTableScreen(WeaponTableContainer inventorySlotsIn, PlayerInventory inventoryPlayer, ITextComponent name) {
        super(inventorySlotsIn, inventoryPlayer, name);
        this.xSize = 196;
        this.ySize = 191;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.minecraft.getTextureManager().bindTexture(TABLE_GUI_TEXTURES);
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
        if (container.hasLava()) {
            this.minecraft.getTextureManager().bindTexture(TABLE_GUI_TEXTURES_LAVA);
            this.blit(i, j, 0, 0, this.xSize, this.ySize);
        }
        if (container.isMissingLava()) {
            this.minecraft.getTextureManager().bindTexture(TABLE_GUI_TEXTURES_MISSING_LAVA);
            this.blit(i, j, 0, 0, this.xSize, this.ySize);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        this.font.drawString(title.getFormattedText(), this.xSize / 2f - this.font.getStringWidth(title.getString()) / 2f, 6.0F, 0x404040);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 94), 0x404040);
    }
}
