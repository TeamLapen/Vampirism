package de.teamlapen.vampirism.common.integration.guide.pages;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import de.maxanier.guideapi.api.GuideBookScreen;
import de.maxanier.guideapi.api.book.Book;
import de.maxanier.guideapi.api.category.CategoryBase;
import de.maxanier.guideapi.api.entry.EntryBase;
import de.maxanier.guideapi.api.pages.Page;
import de.teamlapen.faction.common.util.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

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
    @OnlyIn(Dist.CLIENT)
    public void draw(@NotNull GuiGraphics guiGraphics, Book book, CategoryBase category, EntryBase entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuideBookScreen guiBase, Font font) {
        float charWidth = font.width("W");
        int y = guiTop + 12;
        int x = guiLeft + 39;
        if (headline != null) {
            guiGraphics.drawString(font, headline.withStyle(ChatFormatting.BOLD), x, y, 0, false);
            y += font.lineHeight;
        }
        drawLine(guiGraphics, x, y + font.lineHeight, x + (guiBase.xSize() * 3F / 5F), y + font.lineHeight);
        for (Component[] l : lines) {
            x = guiLeft + 39;
            for (int i = 0; i < l.length; i++) {
                int mw = (int) (width[i] * charWidth);
                int aw = font.width(l[i]);
                int dw = (mw - aw) / 2;
                guiGraphics.drawString(font, l[i], x + dw, y, 0, false);
                x += mw;
            }
            y += font.lineHeight;

        }

    }


    protected void drawLine(@NotNull GuiGraphics guiGraphics, double x1, double y1, double x2, double y2) {
        guiGraphics.fill((int) x1, (int) y1, (int) x2, (int) y2, Color.WHITE.getRGB()); //TODO test and potentially inline
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
