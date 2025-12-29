package de.teamlapen.vampirism.client.renderer.tooltips;

import de.teamlapen.vampirism.common.world.items.component.QuarrelPouchContents;
import de.teamlapen.vampirism.common.world.items.tooltip.QuarrelPouchTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;

public class QuarrelPouchClientTooltip extends ClientBundleTooltip {

    public QuarrelPouchClientTooltip(QuarrelPouchTooltip tooltip) {
        super(convert(tooltip));
    }

    private static BundleContents convert(QuarrelPouchTooltip tooltip) {
        return new BundleContents(tooltip.contents().items(), Fraction.getFraction(tooltip.contents().getCount(), QuarrelPouchContents.MAX_ITEMS), -1);
    }
}
