package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.container.AlchemicalCauldronContainer;
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render
                (stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1, 1, 1, 1);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(stack, i, j, 0, 0, this.imageWidth, this.imageHeight);

        int k = menu.getLitProgress();
        if (k > 0) this.blit(stack, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);

        int l = menu.getBurnProgress();
        this.blit(stack, i + 79, j + 34, 176, 14, l + 1, 16);
        l = l / 24 * 30;
        this.blit(stack, i + 142, j + 28 + 30 - l, 176, 60 - l, 12, l);
    }

    @Override
    protected void renderLabels(MatrixStack stack, int mouseX, int mouseY) {
        ITextComponent name = new TranslationTextComponent("tile.vampirism.alchemical_cauldron.display", minecraft.player.getDisplayName().copy().withStyle(TextFormatting.DARK_BLUE), ModBlocks.ALCHEMICAL_CAULDRON.get().getName());
        this.font.draw(stack, name, 5, 6, 0x404040);
        this.font.draw(stack, this.inventory.getDisplayName(), (float) this.inventoryLabelX, (float) this.inventoryLabelY, 4210752);
    }

}
