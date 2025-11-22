package de.teamlapen.vampirism.misc.extension.client;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public interface IGuiGraphics {

    void vampirism$drawCenteredString(Font font, Component text, int x, int y, int color, boolean shadow);
}
