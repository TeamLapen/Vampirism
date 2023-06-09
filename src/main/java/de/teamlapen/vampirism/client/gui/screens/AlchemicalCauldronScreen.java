package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.AlchemicalCauldronMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;


@OnlyIn(Dist.CLIENT)
public class AlchemicalCauldronScreen extends AbstractContainerScreen<AlchemicalCauldronMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("vampirism:textures/gui/alchemical_cauldron.png");

    public AlchemicalCauldronScreen(@NotNull AlchemicalCauldronMenu inventorySlotsIn, @NotNull Inventory inventoryPlayer, @NotNull Component name) {
        super(inventorySlotsIn, inventoryPlayer, name);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1, 1, 1, 1);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);

        int k = menu.getLitProgress();
        if (k > 0) graphics.blit(BACKGROUND, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);

        int l = menu.getBurnProgress();
        graphics.blit(BACKGROUND, i + 79, j + 34, 176, 14, l + 1, 16);
        l = (int) (l / 24F * 30F);
        graphics.blit(BACKGROUND, i + 142, j + 28 + 30 - l, 176, 60 - l, 12, l);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        Component name = Component.translatable("tile.vampirism.alchemical_cauldron.display", minecraft.player.getDisplayName().copy().withStyle(ChatFormatting.DARK_BLUE), ModBlocks.ALCHEMICAL_CAULDRON.get().getName());
        graphics.drawString(this.font, name, 5, 6, 0x404040, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

}
