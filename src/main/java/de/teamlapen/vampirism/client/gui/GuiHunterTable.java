package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.inventory.HunterTableContainer;
import de.teamlapen.vampirism.util.REFERENCE;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Gui for the hunter table
 */
@OnlyIn(Dist.CLIENT)
public class GuiHunterTable extends GuiContainer {
    private static final ResourceLocation altarGuiTextures = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_table.png");
    private final HunterTableContainer container;

    public GuiHunterTable(HunterTableContainer inventorySlotsIn) {
        super(inventorySlotsIn);
        container = inventorySlotsIn;
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
        String string = container.getHunterInventory().hasCustomName() ? this.container.getHunterInventory().getName().toString() : I18n.format(ModBlocks.hunter_table.getTranslationKey());
        this.fontRenderer.drawString(string, 8, 6, 0x404040);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 94, 0x404040);

        String text = null;
        if (!container.isLevelValid()) {
            text = I18n.format("text.vampirism.ritual_level_wrong");
        } else if (!container.getMissingItems().isEmpty()) {
            ItemStack missing = container.getMissingItems();
            ITextComponent item = missing.getItem().equals(ModItems.pure_blood) ? ModItems.pure_blood.getDisplayName(missing) : new TextComponentTranslation(missing.getTranslationKey() + ".name");
            text = I18n.format("text.vampirism.ritual_missing_items", missing.getCount(), item.getUnformattedComponentText());
        }
        if (text != null) this.fontRenderer.drawSplitString(text, 8, 50, this.xSize - 10, 0x000000);
    }
}
