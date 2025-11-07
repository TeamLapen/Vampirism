package de.teamlapen.vampirism.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
        int width = (int) ((1f - this.progress) * this.getWidth());
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(true, this.isHoveredOrFocused() && progress == 0f), this.getX(), this.getY(), width, this.getHeight());
        int i = getFGColor();
        this.renderString(guiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public void updateState(float progress) {
        this.active = progress == 0;
        this.progress = progress;
    }

}
