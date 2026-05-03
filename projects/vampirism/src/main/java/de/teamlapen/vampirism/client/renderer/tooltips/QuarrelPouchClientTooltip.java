package de.teamlapen.vampirism.client.renderer.tooltips;

import de.teamlapen.vampirism.common.world.items.tooltip.QuarrelPouchTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;

public class QuarrelPouchClientTooltip extends ClientBundleTooltip {

    public QuarrelPouchClientTooltip(QuarrelPouchTooltip tooltip) {
        super(convert(tooltip));
    }

    private static BundleContents convert(QuarrelPouchTooltip tooltip) {
        return new BundleContents(tooltip.contents().items().stream().map(x -> new ItemStackTemplate(x.getItem(), x.count())).toList()/*, Fraction.getFraction(tooltip.contents().getCount(), QuarrelPouchContents.MAX_ITEMS), -1*/);
    }
}
