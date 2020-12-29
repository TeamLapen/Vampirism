package de.teamlapen.vampirism.modcompat.guide.pages;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.Page;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.gui.BaseScreen;
import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
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
    private final List<String[]> lines;
    /**
     * Max char count in one cell for each column
     */
    private final int[] width;
    private final IFormattableTextComponent headline;

    private PageTable(List<String[]> lines, int[] width, IFormattableTextComponent headline) {
        this.lines = lines;
        this.width = width;
        this.headline = headline;
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(MatrixStack stack, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, FontRenderer fontRendererObj) {
        float charWidth = fontRendererObj.getStringWidth("W");
        int y = guiTop + 12;
        int x = guiLeft + 39;
        if (headline != null) {
            fontRendererObj.func_243248_b(stack, headline.mergeStyle(TextFormatting.BOLD), x, y, 0);
            y += fontRendererObj.FONT_HEIGHT;
        }
        drawLine(stack, x, y + fontRendererObj.FONT_HEIGHT, x + (guiBase.xSize * 3F / 5F), y + fontRendererObj.FONT_HEIGHT, guiBase.publicZLevel);
        for (String[] l : lines) {
            x = guiLeft + 39;
            for (int i = 0; i < l.length; i++) {
                int mw = (int) (width[i] * charWidth);
                int aw = fontRendererObj.getStringWidth(l[i]);
                int dw = (mw - aw) / 2;
                fontRendererObj.drawString(stack, l[i], x + dw, y, 0);
                x += mw;
            }
            y += fontRendererObj.FONT_HEIGHT;

        }

    }

    /**
     * Copied from GuiPieMenu
     */
    protected void drawLine(MatrixStack stack, double x1, double y1, double x2, double y2, float publicZLevel) {
        stack.push();
        Matrix4f matrix = stack.getLast().getMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        RenderSystem.lineWidth(2F);
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(matrix, (float) x1, (float) y1, publicZLevel).color(0, 0, 0, 255).endVertex();
        builder.pos(matrix, (float) x2, (float) y2, publicZLevel).color(0, 0, 0, 255).endVertex();
        Tessellator.getInstance().draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        stack.pop();
    }


    public static class Builder {
        int columns;
        List<String[]> lines;
        IFormattableTextComponent headline;

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
            //noinspection ConfusingArgumentToVarargsMethod
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

        public Builder setHeadline(IFormattableTextComponent s) {
            headline = s;
            return this;
        }


    }
}
