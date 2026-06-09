package de.teamlapen.vampirism.client.renderer.tooltips;

import de.teamlapen.vampirism.common.world.items.component.QuarrelPouchContents;
import de.teamlapen.vampirism.common.world.items.tooltip.QuarrelPouchTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;

import java.util.List;

public class QuarrelPouchClientTooltip extends ClientBundleTooltip {

    private static final Component EMPTY_DESCRIPTION = Component.translatable("tooltip.vampirism.quarrel_pouch.empty");

    private final boolean empty;

    public QuarrelPouchClientTooltip(QuarrelPouchTooltip tooltip) {
        super(convert(tooltip.contents()));
        this.empty = tooltip.contents().isEmpty();
    }

    @Override
    public int getHeight(Font font) {
        if (!this.empty) {
            return super.getHeight(font);
        }
        int belowDescription = getEmptyBundleBackgroundHeight(font) - getEmptyBundleDescriptionTextHeight(font);
        return descriptionHeight(font) + belowDescription;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        if (!this.empty) {
            super.extractImage(font, x, y, width, height, graphics);
            return;
        }
        int left = x + (width - getWidth(font)) / 2;
        graphics.textWithWordWrap(font, EMPTY_DESCRIPTION, left, y, getWidth(font), -5592406);
        extractProgressbar(left, y + descriptionHeight(font) + 4, font, graphics, Fraction.ZERO);
    }

    private int descriptionHeight(Font font) {
        return font.split(EMPTY_DESCRIPTION, getWidth(font)).size() * font.lineHeight;
    }

    private static BundleContents convert(QuarrelPouchContents contents) {
        List<ItemStackTemplate> items = contents.items().stream().map(stack -> {
            ItemStack scaled = stack.copy();
            scaled.set(DataComponents.MAX_STACK_SIZE, QuarrelPouchContents.MAX_ITEMS);
            return ItemStackTemplate.fromNonEmptyStack(scaled);
        }).toList();
        return new BundleContents(items);
    }
}