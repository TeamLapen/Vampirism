package de.teamlapen.lib.lib.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.LIBREFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ScreenUtils;
import org.jetbrains.annotations.NotNull;

public class ProgressBar extends AbstractWidget {

    private static final ResourceLocation WIDGETS = new ResourceLocation(LIBREFERENCE.MODID, "textures/gui/widgets.png");
    private float progress = 0;
    private int color = 0xFFFFFF;

    public ProgressBar( int x, int y, int width, @NotNull Component title) {
        super(x, y, width, 20, title);
    }


    @Override
    public void renderWidget(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int i = this.getTextureY();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        ScreenUtils.blitWithBorder(matrixStack, WIDGETS, this.getX(), this.getY(), 0, 46 + i * 20, this.width, 20, 200, 20, 3, 0);

        RenderSystem.setShaderColor((color >> 16) / 256f, ((color >> 8) & 0xFF) / 256f, (color & 0xFF) / 256f, this.alpha);
        RenderSystem.setShaderTexture(0, WIDGETS);
        if (this.active) blit(matrixStack, this.getX() + 3, this.getY() + 3, 0, 32, (int) ((progress) * (this.width - 6)), 14);
//        this.renderBg(matrixStack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(matrixStack, font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);

        setTooltip(Tooltip.create(Component.literal("" + ((int) (progress * 100f)) + "%")));
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
