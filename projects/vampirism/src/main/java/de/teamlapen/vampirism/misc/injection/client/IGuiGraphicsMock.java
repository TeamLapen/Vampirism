package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IGuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public interface IGuiGraphicsMock extends IGuiGraphics {
    @Override
    default void vampirism$drawCenteredString(Font font, Component text, int x, int y, int color, boolean shadow) {

    }
}
