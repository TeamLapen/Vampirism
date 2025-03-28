package de.teamlapen.vampirism.items.tooltip;

import de.teamlapen.vampirism.items.component.QuarrelPouchContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record QuarrelPouchTooltip(QuarrelPouchContents contents) implements TooltipComponent {
}
