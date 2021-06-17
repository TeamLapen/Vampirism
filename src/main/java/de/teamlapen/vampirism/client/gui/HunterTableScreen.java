package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.inventory.container.HunterTableContainer;
import de.teamlapen.vampirism.items.PureBloodItem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Gui for the hunter table
 */
@OnlyIn(Dist.CLIENT)
public class HunterTableScreen extends ContainerScreen<HunterTableContainer> {
    private static final ResourceLocation altarGuiTextures = new ResourceLocation(REFERENCE.MODID, "textures/gui/hunter_table.png");
    private final IWorldPosCallable worldPos;

    public HunterTableScreen(HunterTableContainer inventorySlotsIn, PlayerInventory playerInventory, ITextComponent name) {
        this(inventorySlotsIn, playerInventory, name, IWorldPosCallable.DUMMY);
    }

    public HunterTableScreen(HunterTableContainer inventorySlotsIn, PlayerInventory playerInventory, ITextComponent name, IWorldPosCallable worldPosIn) {
        super(inventorySlotsIn, playerInventory, name);
        this.worldPos = worldPosIn;
    }

    @Override
    public void render(@Nonnull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(stack, mouseX, mouseY);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack stack, float var1, int var2, int var3) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(altarGuiTextures);
        this.blit(stack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack stack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(stack, mouseX, mouseY);

        ITextComponent text = null;
        if (!container.isLevelValid(false)) {
            text = new TranslationTextComponent("container.vampirism.hunter_table.level_wrong");
        } else if (!container.isLevelValid(true)) {
            text = new TranslationTextComponent("container.vampirism.hunter_table.structure_level_wrong");
        } else if (!container.getMissingItems().isEmpty()) {
            ItemStack missing = container.getMissingItems();
            ITextComponent item = missing.getItem() instanceof PureBloodItem ? ((PureBloodItem) missing.getItem()).getCustomName() : new TranslationTextComponent(missing.getTranslationKey());
            text = new TranslationTextComponent("text.vampirism.hunter_table.ritual_missing_items", missing.getCount(), item);
        }
        if (text != null) this.font.func_238418_a_(text, 8, 50, this.xSize - 10, 0x000000);
    }
}
