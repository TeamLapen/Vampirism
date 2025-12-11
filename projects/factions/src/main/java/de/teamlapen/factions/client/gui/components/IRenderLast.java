package de.teamlapen.factions.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public interface IRenderLast {

    void renderLast(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
}
