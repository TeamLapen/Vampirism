package de.teamlapen.vampirism.client.renderer.tooltips;

import de.teamlapen.vampirism.common.world.items.component.QuarrelPouchContents;
import de.teamlapen.vampirism.common.world.items.tooltip.QuarrelPouchTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;

import java.util.List;

public class QuarrelPouchClientTooltip extends ClientBundleTooltip {

    public QuarrelPouchClientTooltip(QuarrelPouchTooltip tooltip) {
        super(convert(tooltip.contents()));
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