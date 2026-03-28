package de.teamlapen.vampirism.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class CooldownButton extends Button {
    private float progress = 1f;

    public CooldownButton(int x, int y, int width, int height, @NotNull Component title, @NotNull OnPress pressedAction) {
        super(x, y, width, height, title, pressedAction, Button.DEFAULT_NARRATION);
    }

    @Override
    protected void renderContents(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
        int width = (int) ((1f - this.progress) * this.getWidth());
        GuiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(true, this.isHoveredOrFocused() && progress == 0f), this.getX(), this.getY(), width, this.getHeight());
        int i = getFGColor();

        renderDefaultLabel(GuiGraphicsExtractor.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
    }

    public void updateState(float progress) {
        this.active = progress == 0;
        this.progress = progress;
    }

}
