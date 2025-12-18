package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IGuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Deprecated
public interface IGuiGraphicsVampirismMock extends IGuiGraphics {
    @Override
    default void vampirism$drawCenteredString(Font font, Component text, int x, int y, int color, boolean shadow) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void vampirism$blitSpriteTiledOffset(ResourceLocation texture, int x, int y, int width, int height, int xOffset, int yOffset, int color) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
