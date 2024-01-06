package de.teamlapen.lib.lib.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

public class UtilLibClient {

    /**
     * Draws a TextComponent split over multiple lines
     *
     * @return The height of the rendered text
     */
    public static int renderMultiLine(@NotNull Font fontRenderer, @NotNull GuiGraphics graphics, @NotNull Component text, int textLength, int x, int y, int color) {
        int d = 0;
        for (FormattedCharSequence sequence : fontRenderer.split(text, textLength)) {
            graphics.drawString(fontRenderer, sequence, x, y + d, color, false);
            d += fontRenderer.lineHeight;
        }
        return d;
    }
}
