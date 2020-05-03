package de.teamlapen.vampirism.modcompat.guide.pages;


import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.Page;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.gui.BaseScreen;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Book page containing a table and an optional headline
 *
 * @author Maxanier
 */
public class PageTable extends Page {
    private List<String[]> lines;
    /**
     * Max char count in one cell for each column
     */
    private int[] width;
    private String headline;

    private PageTable(List<String[]> lines, int[] width, String headline) {
        this.lines = lines;
        this.width = width;
        this.headline = headline;
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, FontRenderer fontRendererObj) {
        float charWidth = fontRendererObj.getCharWidth('W');
        int y = guiTop + 12;
        int x = guiLeft + 39;
        if (headline != null) {
            fontRendererObj.drawString("§l" + headline, x, y, 0);
            y += fontRendererObj.FONT_HEIGHT;
        }
        drawLine(x, y + fontRendererObj.FONT_HEIGHT, x + (guiBase.xSize * 3F / 5F), y + fontRendererObj.FONT_HEIGHT, guiBase.publicZLevel);
        for (String[] l : lines) {
            x = guiLeft + 39;
            for (int i = 0; i < l.length; i++) {
                int mw = (int) (width[i] * charWidth);
                int aw = fontRendererObj.getStringWidth(l[i]);
                int dw = (mw - aw) / 2;
                fontRendererObj.drawString(l[i], x + dw, y, 0);
                x += mw;
            }
            y += fontRendererObj.FONT_HEIGHT;

        }

    }

    /**
     * Copied from GuiPieMenu
     */
    protected void drawLine(double x1, double y1, double x2, double y2, float publicZLevel) {
        GlStateManager.pushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GlStateManager.color4f(0F, 0F, 0F, 1F);
        GlStateManager.lineWidth(2F);
        GlStateManager.begin(GL11.GL_LINES);
        GlStateManager.vertex3f((float) x1, (float) y1, publicZLevel);
        GlStateManager.vertex3f((float) x2, (float) y2, publicZLevel);
        GlStateManager.end();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.color4f(0F, 0F, 0F, 1F);
        GlStateManager.popMatrix();
    }


    public static class Builder {
        int columns;
        List<String[]> lines;
        String headline;

        public Builder(int columns) {
            this.columns = columns;
            lines = new ArrayList<>();
        }

        public Builder addLine(Object... objects) {
            if (objects.length != columns) {
                throw new IllegalArgumentException("Every added line as to contain one String for every column");
            }
            String[] l = new String[objects.length];
            for (int i = 0; i < objects.length; i++) {
                l[i] = String.valueOf(objects[i]);
            }
            lines.add(l);
            return this;
        }

        public Builder addUnlocLine(String... strings) {
            String[] loc = new String[strings.length];
            for (int i = 0; i < strings.length; i++) {
                loc[i] = UtilLib.translate(strings[i]);
            }
            return addLine(loc);
        }

        public PageTable build() {
            int[] width = new int[columns];
            for (int i = 0; i < columns; i++) {
                int max = 0;
                for (String[] s : lines) {
                    int w = s[i].length();
                    if (w > max) max = w;
                }
                width[i] = max;
            }
            return new PageTable(lines, width, headline);
        }

        public Builder setHeadline(String s) {
            headline = s;
            return this;
        }


    }
}
