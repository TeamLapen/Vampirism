package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.inventory.PotionTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class PotionTableScreen extends AbstractContainerScreen<PotionTableMenu> {

    private static final int[] BUBBLELENGTHS = new int[] {29, 24, 20, 16, 11, 6, 0};
    public static final ResourceLocation BACKGROUND = VResourceLocation.mod("textures/gui/container/potion_table.png");
    private static final ResourceLocation BACKGROUND_EXTENDED = VResourceLocation.mod("textures/gui/container/potion_table_extended.png");
    public static final ResourceLocation FUEL_SPRITE = VResourceLocation.mod("container/potion_table/fuel");
    public static final ResourceLocation PROGRESS_SPRITE = VResourceLocation.mod("container/potion_table/progress");
    public static final ResourceLocation BUBBLES_SPRITE = VResourceLocation.mod("container/potion_table/bubbles");


    public PotionTableScreen(@NotNull PotionTableMenu screenContainer, @NotNull Inventory inv, @NotNull Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.titleLabelY = 5;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        ResourceLocation texture = this.menu.isExtendedTable() ? BACKGROUND_EXTENDED : BACKGROUND;

        int cX = (this.width - this.imageWidth) / 2;
        int cY = (this.height - this.imageHeight) / 2;
        graphics.blit(texture, cX, cY, 0, 0, this.imageWidth, this.imageHeight);
        int fuelTime = this.menu.getFuelTime();
        int fuelIconWidth = Mth.clamp((18 * fuelTime + 20 - 1) / 20, 0, 18);
        if (fuelIconWidth > 0) {
            graphics.blitSprite(FUEL_SPRITE, 18, 4, 0, 0, cX + 66, cY + 41, fuelIconWidth, 4);
        }

        int brewTime = this.menu.getBrewTime();
        if (brewTime > 0) {
            int brewIconHeight = (int) (28.0F * (1.0F - (float) brewTime / 400.0F));
            if (brewIconHeight > 0) {
                graphics.blitSprite(PROGRESS_SPRITE, 9, 28, 0, 0, cX + 145, cY + 17, 12, brewIconHeight);
            }

            brewIconHeight = BUBBLELENGTHS[brewTime / 2 % 7];
            if (brewIconHeight > 0) {
                graphics.blitSprite(BUBBLES_SPRITE, 12, 29, 0, 29 - brewIconHeight, cX + 69, cY + 14 + 26 - brewIconHeight, 12, brewIconHeight);
            }
        }
    }
}