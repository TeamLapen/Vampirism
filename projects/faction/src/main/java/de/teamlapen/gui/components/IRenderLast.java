package de.teamlapen.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface IRenderLast {

    void renderLast(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);
}
