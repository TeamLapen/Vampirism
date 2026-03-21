package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.inventory.VaporStillMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class VaporStillScreen extends AbstractContainerScreen<VaporStillMenu> {

    public static final Identifier BACKGROUND_NORMAL = VIdentifier.mod("textures/gui/container/vapor_still.png");
    public static final Identifier BACKGROUND_EXTENDED = VIdentifier.mod("textures/gui/container/vapor_still_extended.png");

    public static final Identifier SPRITE_FUEL = VIdentifier.mod("container/vapor_still/fuel_height");
    public static final Identifier SPRITE_FLAMES = VIdentifier.mod("container/vapor_still/flames");
    public static final Identifier SPRITE_PROGRESS = VIdentifier.mod("container/vapor_still/distillation_progress");

    private static final int FUEL_SPRITE_WIDTH = 4, FUEL_SPRITE_HEIGHT = 16;
    private static final int FLAMES_SPRITE_WIDTH = 26, FLAMES_SPRITE_HEIGHT = 15;
    private static final int PROGRESS_SPRITE_WIDTH = 9, PROGRESS_SPRITE_HEIGHT = 29;

    private static final int FLAMES_FRAME_COUNT = 7;

    public VaporStillScreen(@NotNull VaporStillMenu menu, @NotNull Inventory playerInventory, @NotNull Component title) {
        super(menu, playerInventory, title);
        this.titleLabelY = 5;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        Identifier background = menu.isExtended() ? BACKGROUND_EXTENDED : BACKGROUND_NORMAL;
        graphics.blit(RenderPipelines.GUI_TEXTURED, background, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        renderFuel(graphics);
        renderFlames(graphics);
        renderBrewProgress(graphics);
    }

    private void renderFuel(GuiGraphics graphics) {
        int fuelTime = menu.getFuelTime();
        int fuelHeight = Mth.clamp((FUEL_SPRITE_HEIGHT * fuelTime + 20 - 1) / 20, 0, FUEL_SPRITE_HEIGHT);
        if (fuelHeight > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_FUEL, FUEL_SPRITE_WIDTH, FUEL_SPRITE_HEIGHT, 0, FUEL_SPRITE_HEIGHT - fuelHeight, leftPos + 153, topPos + 58 + FUEL_SPRITE_HEIGHT - fuelHeight, FUEL_SPRITE_WIDTH, fuelHeight);
        }
    }

    private void renderFlames(GuiGraphics graphics) {
        int brewTime = menu.getBrewTime();
        if (brewTime <= 0) return;

        int frame = brewTime / 2 % FLAMES_FRAME_COUNT;
        int flameHeight = FLAMES_SPRITE_HEIGHT - (frame * FLAMES_SPRITE_HEIGHT / (FLAMES_FRAME_COUNT - 1));
        if (flameHeight <= 0) return;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_FLAMES, FLAMES_SPRITE_WIDTH, FLAMES_SPRITE_HEIGHT, 0, FLAMES_SPRITE_HEIGHT - flameHeight, leftPos + 129, topPos + 40 + FLAMES_SPRITE_HEIGHT - flameHeight, FLAMES_SPRITE_WIDTH, flameHeight);
    }

    private void renderBrewProgress(GuiGraphics graphics) {
        int brewTime = menu.getBrewTime();
        if (brewTime <= 0) return;

        int arrowHeight = (int) (PROGRESS_SPRITE_HEIGHT * (1f - (float) brewTime / menu.getMaxBrewTime()));
        if (arrowHeight > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_PROGRESS, PROGRESS_SPRITE_WIDTH, PROGRESS_SPRITE_HEIGHT, 0, 0, leftPos + 101, topPos + 16, PROGRESS_SPRITE_WIDTH, arrowHeight);
        }
    }
}
