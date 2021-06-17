package de.teamlapen.lib.lib.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.LIBREFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class ProgressBar extends Widget {

    private static final ResourceLocation WIDGETS = new ResourceLocation(LIBREFERENCE.MODID, "textures/gui/widgets.png");
    private final Screen screen;
    private float progress = 0;
    private int color = 0xFFFFFF;

    public ProgressBar(Screen screen, int x, int y, int width, ITextComponent title) {
        super(x, y, width, 20, title);
        this.screen = screen;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(WIDGETS);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        GuiUtils.drawContinuousTexturedBox(matrixStack, WIDGETS, x, y, 0, 46 + i * 20, this.width, 20, 200, 20, 3, 0);
        RenderSystem.color4f((color >> 16) / 256f, ((color >> 8) & 0xFF) / 256f, (color & 0xFF) / 256f, this.alpha);
        minecraft.getTextureManager().bindTexture(WIDGETS);
        if (this.active) blit(matrixStack, x + 3, y + 3, 0, 32, (int) ((progress) * (this.width - 6)), 14);
        this.renderBg(matrixStack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(matrixStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);

        if (isHovered()) {
            renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (this.active)
            screen.renderTooltip(matrixStack, new StringTextComponent("" + ((int) (progress * 100f)) + "%"), mouseX, mouseY);

    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setProgress(float p) {
        this.progress = p;
    }
}
