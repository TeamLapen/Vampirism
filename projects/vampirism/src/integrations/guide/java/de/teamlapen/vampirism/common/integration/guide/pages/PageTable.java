package de.teamlapen.vampirism.common.integration.guide.pages;


import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.pages.Page;
import de.teamlapen.faction.common.util.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Book page containing a table and an optional headline
 *
 * @author Maxanier
 */
public class PageTable extends Page {
    private final List<Component[]> lines;
    /**
     * Max char count in one cell for each column
     */
    private final int[] width;
    private final MutableComponent headline;

    private PageTable(List<Component[]> lines, int[] width, MutableComponent headline) {
        this.lines = lines;
        this.width = width;
        this.headline = headline;
    }


    @Override
    public void draw(@NotNull GuiGraphics guiGraphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen guiBase, Font font) {
        float charWidth = font.width("W");
        int x = guiLeft + 39;
        int y = guiTop + 12;
        if (headline != null) {
            guiGraphics.drawString(font, headline.withStyle(ChatFormatting.BOLD), x, y, book.getTextColor(), false);
            y += font.lineHeight;
        }
        drawLine(guiGraphics, x, y + font.lineHeight, (int) (x + (guiBase.xSize() - 2 * 39)), y + font.lineHeight);
        y += 2;
        for (Component[] l : lines) {
            x = guiLeft + 39;
            for (int i = 0; i < l.length; i++) {
                int mw = (int) (width[i] * charWidth);
                int aw = font.width(l[i]);
                int dw = (mw - aw) / 2;
                guiGraphics.drawString(font, l[i], x + dw, y, book.getTextColor(), false);
                x += mw;
            }
            y += font.lineHeight;

        }

    }


    protected void drawLine(@NotNull GuiGraphics guiGraphics, int x1, int y1, int x2, int y2) {
        guiGraphics.fill(x1, y1, x2 + 1, y2 + 1, Color.GRAY.getRGB()); //TODO test and potentially inline
    }


    public static class Builder {
        int columns;
        List<Component[]> lines;
        MutableComponent headline;

        public Builder(int columns) {
            this.columns = columns;
            lines = new ArrayList<>();
        }

        public @NotNull Builder addLine(Component @NotNull ... objects) {
            if (objects.length != columns) {
                throw new IllegalArgumentException("Every added line as to contain one String for every column");
            }
            lines.add(objects);
            return this;
        }

        public @NotNull Builder addLine(Object @NotNull ... objects) {
            return addLine(Arrays.stream(objects).map(object -> {
                if (object instanceof Component comp) return comp;
                return Component.literal(String.valueOf(object));
            }).toArray(Component[]::new));
        }

        public @NotNull PageTable build() {
            int[] width = new int[columns];
            for (int i = 0; i < columns; i++) {
                int max = 0;
                for (Component[] s : lines) {
                    int w = s[i].getString().length();
                    if (w > max) max = w;
                }
                width[i] = max;
            }
            return new PageTable(lines, width, headline);
        }

        public @NotNull Builder setHeadline(MutableComponent s) {
            headline = s;
            return this;
        }


    }
}
