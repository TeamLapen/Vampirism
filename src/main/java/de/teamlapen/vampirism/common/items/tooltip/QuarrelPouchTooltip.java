package de.teamlapen.vampirism.common.items.tooltip;

import de.teamlapen.vampirism.common.items.component.QuarrelPouchContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record QuarrelPouchTooltip(QuarrelPouchContents contents) implements TooltipComponent {
}
