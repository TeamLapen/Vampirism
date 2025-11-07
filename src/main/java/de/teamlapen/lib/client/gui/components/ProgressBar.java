package de.teamlapen.lib.client.gui.components;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ProgressBar extends AbstractWidget {

    private static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "widgets/progress_bar"), ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "widgets/progress_bar_inactive"), ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "widgets/progress_bar_highlighted"));
    private static final ResourceLocation PROGRESS = ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "widgets/progress_bar_progress");
    private float progress = 0;
    private int color = 0xFFFFFF;

    public ProgressBar(int x, int y, int width, @NotNull Component title) {
        super(x, y, width, 20, title);
    }


    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.active, progress > 0), this.getX(), this.getY(), this.width, 20, ARGB.color(alpha, color));

        if (progress > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS, this.getX() + 3, this.getY() + 3, (int) ((progress) * (this.width - 6)), 14, ARGB.color(alpha, color));
        }
        int j = getFGColor();
        graphics.drawCenteredString(font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        setTooltip(Tooltip.create(Component.literal(((int) (progress * 100f)) + "%")));
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setProgress(float p) {
        this.progress = p;
    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput p_169152_) {

    }

    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }

        return 46 + i * 20;
    }
}
