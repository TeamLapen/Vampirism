package de.teamlapen.vampirism.misc.extension.client;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public interface IGuiGraphics {

    void vampirism$drawCenteredString(Font font, Component text, int x, int y, int color, boolean shadow);

    void vampirism$blitSpriteTiledOffset(Identifier texture, int x, int y, int width, int height, int xOffset, int yOffset, int color);
}
