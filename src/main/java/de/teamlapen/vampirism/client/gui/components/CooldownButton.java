package de.teamlapen.vampirism.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
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
        guiGraphics.blitWithBorder(SPRITES.get(false, false), this.getX(), this.getY(), 0, 0, this.width, this.height, 200, 20, 3);
        int width = (int) ((1f - progress) * this.width);
        int s = Mth.clamp(width / 2, 0, 3);
        guiGraphics.blitWithBorder(SPRITES.get(this.active, this.isHovered), this.getX(), this.getY(), 0, 0, width, this.height, 200, 20, s);
        int i = getFGColor();
        this.renderString(guiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public void updateState(float progress) {
        this.active = progress == 0;
        this.progress = progress;
    }

}
