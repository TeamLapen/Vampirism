package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.inventory.HunterTableContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

/**
 * Gui for the hunter table
 */
public class GuiHunterTable extends GuiContainer {
    private static final ResourceLocation altarGuiTextures = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunterTable.png");
    private final HunterTableContainer container;

    public GuiHunterTable(HunterTableContainer inventorySlotsIn) {
        super(inventorySlotsIn);
        container = inventorySlotsIn;
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
        String string = container.getHunterInventory().hasCustomName() ? this.container.getHunterInventory().getName() : I18n.format(this.container.getHunterInventory().getName());
        this.fontRendererObj.drawString(string, 8, 6, 0x404040);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 94, 0x404040);

        String text = null;
        if (!container.isLevelValid()) {
            text = I18n.format("text.vampirism.ritual_level_wrong");
        } else if (container.getMissingItems() != null) {
            ItemStack missing = container.getMissingItems();
            IChatComponent item = missing.getItem().equals(ModItems.pureBlood) ? ModItems.pureBlood.getDisplayName(missing) : new ChatComponentTranslation(missing.getUnlocalizedName() + ".name");
            text = I18n.format("text.vampirism.ritual_missing_items", missing.stackSize, item.getUnformattedText());
        }
        if (text != null) this.fontRendererObj.drawSplitString(text, 8, 50, this.xSize - 10, 0x000000);
    }
}
