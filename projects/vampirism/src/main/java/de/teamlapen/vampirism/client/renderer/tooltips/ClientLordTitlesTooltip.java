package de.teamlapen.vampirism.client.renderer.tooltips;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Renders {@link LordTitlesTooltip} as a two column table with a header row. The player's current title is highlighted.
 */
public class ClientLordTitlesTooltip implements ClientTooltipComponent {

    private static final Component DEFAULT_HEADER = Component.translatable("gui.vampirism.appearance.title_default").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);
    private static final Component FEMALE_HEADER = Component.translatable("gui.vampirism.appearance.title_female").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);
    private static final int ROW_HEIGHT = 10;
    private static final int COLUMN_GAP = 12;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int CURRENT_COLOR = 0xFFFFFF55;

    private final LordTitlesTooltip tooltip;

    public ClientLordTitlesTooltip(LordTitlesTooltip tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public int getHeight(Font font) {
        return (rows() + 1) * ROW_HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        return columnWidth(font, DEFAULT_HEADER, this.tooltip.defaultTitles()) + COLUMN_GAP + columnWidth(font, FEMALE_HEADER, this.tooltip.femaleTitles());
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        int secondColumnX = x + columnWidth(font, DEFAULT_HEADER, this.tooltip.defaultTitles()) + COLUMN_GAP;
        graphics.text(font, DEFAULT_HEADER, x, y, TEXT_COLOR, true);
        graphics.text(font, FEMALE_HEADER, secondColumnX, y, TEXT_COLOR, true);

        for (int row = 0; row < rows(); row++) {
            int rowY = y + (row + 1) * ROW_HEIGHT;
            boolean currentRow = row + 1 == this.tooltip.currentLevel();
            graphics.text(font, this.tooltip.defaultTitles().get(row), x, rowY, currentRow && !this.tooltip.female() ? CURRENT_COLOR : TEXT_COLOR, true);
            graphics.text(font, this.tooltip.femaleTitles().get(row), secondColumnX, rowY, currentRow && this.tooltip.female() ? CURRENT_COLOR : TEXT_COLOR, true);
        }
    }

    private int rows() {
        return Math.min(this.tooltip.defaultTitles().size(), this.tooltip.femaleTitles().size());
    }

    private static int columnWidth(Font font, Component header, List<Component> titles) {
        int width = font.width(header);
        for (Component title : titles) {
            width = Math.max(width, font.width(title));
        }
        return width;
    }
}
