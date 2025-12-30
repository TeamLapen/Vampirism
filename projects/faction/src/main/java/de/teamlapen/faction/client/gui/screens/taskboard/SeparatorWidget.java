package de.teamlapen.faction.client.gui.screens.taskboard;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class SeparatorWidget extends AbstractWidget  {

    private final int color;

    public SeparatorWidget(int width) {
        this(width, 0xFF777777);
    }

    public SeparatorWidget(int width, int color) {
        super(0,0, width, 1, Component.empty());
        this.color = color;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(getX(), getY() + 1, getX() + getWidth(), getY() + 2, this.color);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
