package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.inventory.container.HunterTableContainer;
import de.teamlapen.vampirism.items.PureBloodItem;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Gui for the hunter table
 */
@OnlyIn(Dist.CLIENT)
public class HunterTableScreen extends ContainerScreen<HunterTableContainer> {
    private static final ResourceLocation altarGuiTextures = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_table.png");
    private IWorldPosCallable worldPos;

    public HunterTableScreen(HunterTableContainer inventorySlotsIn, PlayerInventory playerInventory, ITextComponent name) {
        this(inventorySlotsIn, playerInventory, name, IWorldPosCallable.DUMMY);
    }

    public HunterTableScreen(HunterTableContainer inventorySlotsIn, PlayerInventory playerInventory, ITextComponent name, IWorldPosCallable worldPosIn) {
        super(inventorySlotsIn, playerInventory, name);
        this.worldPos = worldPosIn;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(altarGuiTextures);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 0x404040);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 94), 0x404040);

        ITextComponent text = null;
        if (!container.isLevelValid()) {
            text = new TranslationTextComponent("container.vampirism.hunter_table.structure_level_wrong");
        } else if (!container.getMissingItems().isEmpty()) {
            ItemStack missing = container.getMissingItems();
            ITextComponent item = missing.getItem() instanceof PureBloodItem ? ((PureBloodItem) missing.getItem()).getCustomName() : new TranslationTextComponent(missing.getTranslationKey());
            text = new TranslationTextComponent("text.vampirism.hunter_table.ritual_missing_items", missing.getCount(), item.getFormattedText());
        }
        if (text != null) this.font.drawSplitString(text.getFormattedText(), 8, 50, this.xSize - 10, 0x000000);
    }
}
