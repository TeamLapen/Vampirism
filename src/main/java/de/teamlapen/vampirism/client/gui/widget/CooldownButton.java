package de.teamlapen.vampirism.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ScreenUtils;
import org.jetbrains.annotations.NotNull;

public class CooldownButton extends Button {
    private float progress = 1f;

    public CooldownButton(int x, int y, int width, int height, @NotNull Component title, @NotNull OnPress pressedAction) {
        super(x, y, width, height, title, pressedAction);
    }

    @Override
    public void renderButton(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontrenderer = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        ScreenUtils.blitWithBorder(matrixStack, WIDGETS_LOCATION, x, y, 0, 46, this.width, this.height, 200, 20, 3, 0);
        int width = (int) ((1f - progress) * this.width);
        int s = Mth.clamp(width / 2, 0, 3);
        ScreenUtils.blitWithBorder(matrixStack, WIDGETS_LOCATION, x, y, 0, this.active && this.isHovered ? 86 : 66, width, this.height, 200, 20, s, 0);
//        this.blit(matrixStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
//        this.blit(matrixStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBg(matrixStack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(matrixStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);

        if (isHovered) {
            renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    public void updateState(float progress) {
        this.active = progress == 0;
        this.progress = progress;
    }


}
