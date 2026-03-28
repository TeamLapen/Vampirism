package de.teamlapen.faction.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;

public interface IRenderLast {

    void renderLast(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);
}
