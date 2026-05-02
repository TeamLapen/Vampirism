package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IGuiGraphicsExtractor;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@Deprecated
public interface IGuiGraphicsExtractorVampirismMock extends IGuiGraphicsExtractor {

    @Override
    default void vampirism$blitSpriteTiledOffset(Identifier texture, int x, int y, int width, int height, int xOffset, int yOffset, int color) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
