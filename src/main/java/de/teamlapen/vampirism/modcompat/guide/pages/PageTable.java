package de.teamlapen.vampirism.modcompat.guide.pages;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.Page;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.gui.BaseScreen;
import de.teamlapen.lib.lib.util.UtilLib;
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
    public void draw(@NotNull GuiGraphics guiGraphics, RegistryAccess registryAccess, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, @NotNull BaseScreen guiBase, @NotNull Font font) {
        float charWidth = font.width("W");
        int y = guiTop + 12;
        int x = guiLeft + 39;
        if (headline != null) {
            guiGraphics.drawString(font, headline.withStyle(ChatFormatting.BOLD), x, y, 0, false);
            y += font.lineHeight;
        }
        drawLine(guiGraphics, x, y + font.lineHeight, x + (guiBase.xSize * 3F / 5F), y + font.lineHeight, 0);
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

    /**
     * Copied from GuiPieMenu
     */
    protected void drawLine(@NotNull GuiGraphics guiGraphics, double x1, double y1, double x2, double y2, float publicZLevel) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        Matrix4f matrix = pose.last().pose();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        RenderSystem.lineWidth(2F);
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(matrix, (float) x1, (float) y1, publicZLevel).color(0, 0, 0, 255).endVertex();
        builder.vertex(matrix, (float) x2, (float) y2, publicZLevel).color(0, 0, 0, 255).endVertex();
        Tesselator.getInstance().end();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        pose.popPose();
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
