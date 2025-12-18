package de.teamlapen.vampirism.common.world.items.tooltip;

import de.teamlapen.vampirism.common.world.items.component.QuarrelPouchContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record QuarrelPouchTooltip(QuarrelPouchContents contents) implements TooltipComponent {
}
