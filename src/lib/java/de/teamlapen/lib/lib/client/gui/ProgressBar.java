package de.teamlapen.lib.lib.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.LIBREFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ScreenUtils;
import org.jetbrains.annotations.NotNull;

public class ProgressBar extends AbstractWidget {

    private static final ResourceLocation WIDGETS = new ResourceLocation(LIBREFERENCE.MODID, "textures/gui/widgets.png");
    private final Screen screen;
    private float progress = 0;
    private int color = 0xFFFFFF;

    public ProgressBar(Screen screen, int x, int y, int width, Component title) {
        super(x, y, width, 20, title);
        this.screen = screen;
    }

    @Override
    public void renderButton(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int i = this.getYImage(this.isHovered);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        ScreenUtils.blitWithBorder(matrixStack, WIDGETS, x, y, 0, 46 + i * 20, this.width, 20, 200, 20, 3, 0);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor((color >> 16) / 256f, ((color >> 8) & 0xFF) / 256f, (color & 0xFF) / 256f, this.alpha);
        RenderSystem.setShaderTexture(0, WIDGETS);
        if (this.active) blit(matrixStack, x + 3, y + 3, 0, 32, (int) ((progress) * (this.width - 6)), 14);
        this.renderBg(matrixStack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(matrixStack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);

        if (isHovered) {
            renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderToolTip(@NotNull PoseStack matrixStack, int mouseX, int mouseY) {
        if (this.active)
            screen.renderTooltip(matrixStack, Component.literal("" + ((int) (progress * 100f)) + "%"), mouseX, mouseY);

    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setProgress(float p) {
        this.progress = p;
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput p_169152_) {

    }
}
